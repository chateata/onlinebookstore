layui.use(['table', 'form', 'jquery', 'layer'], function() {
    var table = layui.table,
        $ = layui.jquery,
        layer = layui.layer,
        form = layui.form;

    var supplierTb = table.render({
        elem: '#supplier_tb',
        url: '/supplier/list',
        cols: [[
            {field:'supplierId', title:'ID', width:100, sort:true},
            {field:'name', title:'名称', width:240},
            {field:'contact', title:'联系人', width:160},
            {field:'address', title:'地址', width:300},
            {fixed:'right', title:'操作', toolbar:'#barDemo', width:160}
        ]],
        page:true,
        height:500
    });

    table.on('tool(supplier_tb)', function(obj){
        var data = obj.data;
        if(obj.event === 'del'){
            layer.confirm('确认删除该供应商?', function(index){
                $.ajax({
                    url: '/supplier/' + data.supplierId,
                    type: 'delete',
                    success: function(res){
                        if(res.code!=0) return layer.msg(res.msg,{icon:2});
                        layer.msg('删除成功',{icon:1}, function(){ obj.del(); });
                    }
                });
                layer.close(index);
            });
        } else if(obj.event === 'edit'){
            layer.open({
                type:1,
                title:'编辑供应商',
                content: $("#supplier_form_tmpl").html(),
                area: ['520px'],
                btn:['更新'],
                yes: function(idx){
                    var new_data = form.val("supplier-form");
                    $.ajax({
                        url: '/supplier/update',
                        type: 'put',
                        data: JSON.stringify(new_data),
                        contentType: 'application/json',
                        success: function(res){
                            if(res.code!=0) return layer.msg(res.msg,{icon:2});
                            layer.msg('更新成功',{icon:1}, function(){ obj.update(new_data); layer.close(idx); });
                        }
                    });
                },
                success: function(){
                    form.render(null,'supplier-form');
                    form.val('supplier-form', data);
                }
            });
        } else if(obj.event === 'manageBooks'){
            // 管理供应商书目
            manageSupplierBooks(data.supplierId, data.name);
        }
    });

    // 供应商书目管理功能
    function manageSupplierBooks(supplierId, supplierName) {
        layer.open({
            type: 1,
            title: supplierName + ' - 书目管理',
            content: $("#supplier_books_tmpl").html(),
            area: ['1200px', '700px'],
            success: function(layero, index){
                var $content = layero.find('.layui-layer-content');

                // 丛书选择事件处理函数
                function handleSeriesChange() {
                    var selectedValue = $content.find('#supplierBookSeriesSelect').val();
                    console.log('Series selection changed to:', selectedValue); // 调试日志
                    if(selectedValue === 'new') {
                        console.log('Showing new series input'); // 调试日志
                        $content.find('#newSeriesInput').css('display', 'block');
                    } else {
                        console.log('Hiding new series input'); // 调试日志
                        $content.find('#newSeriesInput').css('display', 'none');
                        $content.find('input[name="newSeriesName"]').val('');
                    }
                }

                // 使用事件委托绑定change事件
                $content.on('change', '#supplierBookSeriesSelect', function(){
                    handleSeriesChange();
                });

                // 加载丛书数据
                var seriesData = [];
                $.getJSON('/series/list', function(seriesRes){
                    if(seriesRes.code == 0) {
                        seriesData = seriesRes.data || [];
                        var seriesOptions = '<option value="">请选择丛书</option>';
                        seriesOptions += '<option value="none">不属于任何丛书</option>';
                        seriesOptions += '<option value="new">新建丛书系列</option>';
                        layui.each(seriesData, function(i, s){
                            seriesOptions += '<option value="' + s.seriesId + '">' + s.seriesName + '</option>';
                        });
                        $content.find('#supplierBookSeriesSelect').html(seriesOptions);
                        form.render('select');
                    }
                });

                // 初始化供应商书目表格
                var supplierBooksTb = table.render({
                    elem: $content.find('#supplier_books_tb'),
                    url: '/supplier/book/list?supplierId=' + supplierId,
                    cols: [[
                        {field:'supplierBookId', title:'ID', width:80, sort:true},
                        {field:'seriesName', title:'丛书', width:120, templet: function(d){ return d.series ? d.series.seriesName : ''; }},
                        {field:'isbn', title:'ISBN', width:120},
                        {field:'title', title:'书名', width:200},
                        {field:'author', title:'作者', width:120},
                        {field:'press', title:'出版社', width:120},
                        {field:'price', title:'定价', width:80, templet: function(d){ return d.price ? '¥' + d.price : ''; }},
                        {field:'supplyPrice', title:'供货价', width:80, templet: function(d){ return d.supplyPrice ? '¥' + d.supplyPrice : ''; }},
                        {field:'status', title:'状态', width:80, templet: function(d){
                            return d.status === 'ACTIVE' ? '<span class="layui-badge layui-bg-green">活跃</span>' : '<span class="layui-badge layui-bg-gray">停用</span>';
                        }},
                        {fixed:'right', title:'操作', toolbar:'#supplier_books_bar', width:120}
                    ]],
                    page: true,
                    height: 400
                });

                // 搜索供应商书目
                form.on('submit(search_supplier_books)', function(searchData){
                    supplierBooksTb.reload({
                        url: '/supplier/book/search',
                        where: {
                            supplierId: supplierId,
                            isbn: searchData.field.searchIsbn,
                            title: searchData.field.searchTitle
                        },
                        page: {curr: 1}
                    });
                    return false;
                });

                // 添加供应商书目
                $content.find('#add_supplier_book').on('click', function(){
                    editSupplierBook(null, supplierId, supplierBooksTb);
                });

                // 供应商书目表格工具事件
                table.on('tool(supplier_books_tb)', function(bookObj){
                    var bookData = bookObj.data;
                    if(bookObj.event === 'edit_book'){
                        editSupplierBook(bookData, supplierId, supplierBooksTb);
                    } else if(bookObj.event === 'del_book'){
                        layer.confirm('确认删除该书目?', function(bookIndex){
                            $.ajax({
                                url: '/supplier/book/' + bookData.supplierBookId,
                                type: 'delete',
                                success: function(res){
                                    if(res.code!=0) return layer.msg(res.msg,{icon:2});
                                    layer.msg('删除成功',{icon:1}, function(){ bookObj.del(); });
                                }
                            });
                            layer.close(bookIndex);
                        });
                    }
                });
            }
        });
    }

    // 编辑供应商书目
    function editSupplierBook(bookData, supplierId, supplierBooksTb) {
        var isEdit = bookData != null;
        layer.open({
            type: 1,
            title: isEdit ? '编辑书目' : '添加书目',
            content: $("#supplier_book_form_tmpl").html(),
            area: ['600px', '500px'],
            btn: [isEdit ? '更新' : '添加'],
                yes: function(bookIndex){
                    var formData = form.val("supplier-book-form");
                    formData.supplierId = supplierId;

                    // 处理丛书选择
                    if(formData.seriesId === 'none') {
                        formData.seriesId = null;
                    } else if(formData.seriesId === 'new') {
                        // 创建新丛书
                        var newSeriesName = $content.find('input[name="newSeriesName"]').val();
                        if(!newSeriesName || newSeriesName.trim() === '') {
                            return layer.msg('请输入新丛书系列名称',{icon:2});
                        }

                        // 先创建丛书
                        $.ajax({
                            url: '/series/insert',
                            type: 'post',
                            data: JSON.stringify({seriesName: newSeriesName.trim()}),
                            contentType: 'application/json',
                            async: false, // 同步执行，确保丛书创建完成后再继续
                            success: function(seriesRes){
                                if(seriesRes.code != 0) {
                                    layer.msg('创建丛书失败：' + seriesRes.msg,{icon:2});
                                    return false;
                                }
                                formData.seriesId = seriesRes.data; // 使用新创建的丛书ID
                            },
                            error: function(){
                                layer.msg('创建丛书失败',{icon:2});
                                return false;
                            }
                        });
                    }

                    $.ajax({
                        url: isEdit ? '/supplier/book/update' : '/supplier/book/insert',
                        type: isEdit ? 'put' : 'post',
                        data: JSON.stringify(formData),
                        contentType: 'application/json',
                        success: function(res){
                            if(res.code!=0) return layer.msg(res.msg,{icon:2});
                            layer.msg(isEdit ? '更新成功' : '添加成功',{icon:1}, function(){
                                supplierBooksTb.reload();
                                layer.close(bookIndex);
                            });
                        }
                    });
                },
            success: function(){
                form.render(null, 'supplier-book-form');
                if(isEdit){
                    form.val('supplier-book-form', bookData);
                }
            }
        });
    }

});




