layui.use(['table', 'form', 'jquery', 'layer', 'laytpl'], function() {
    var table = layui.table,
        $ = layui.jquery,
        layer = layui.layer,
        laytpl = layui.laytpl,
        form = layui.form;

    var poTb = table.render({
        elem: '#po_tb',
        url: '/purchase/list',
        cols: [[
            {field:'poId', title:'PO ID', width:120, sort:true},
            {field:'supplier', title:'供应商', width:200, templet: function(res){ return res.supplier ? res.supplier.name : ''; }},
            {field:'orderDate', title:'下单时间', width:180},
            {field:'expectedArrivalDate', title:'到货时间', width:160},
            {field:'status', title:'状态', width:120},
            {fixed:'right', title:'操作', toolbar:'#po_bar', width:140}
        ]],
        page:true,
        height: 480
    });

    table.on('tool(po_tb)', function(obj){
        var data = obj.data;
        if(obj.event === 'detail'){
            // fetch full PO details and show in modal
            $.getJSON('/purchase/list?poId=' + data.poId, function(res){
                if(res.code != 0) return layer.msg(res.msg,{icon:2});
                var po = res.data;
                var html = '<div style="padding:16px;"><table class="layui-table"><thead><tr><th>书ID</th><th>书名</th><th>单价(元)</th><th>数量</th><th>总价(元)</th><th>操作</th></tr></thead><tbody>';
                layui.each(po.items || [], function(i,item){
                    var qty = item.quantity || 0;
                    var received = item.receivedQuantity || 0;
                    var remaining = qty - received;
                    var unitPrice = item.unitPrice || (item.unitPrice==0?0: (item.unitPrice||0));
                    var bookName = '';
                    if (item.supplierBook && item.supplierBook.title) {
                        bookName = '[供应商] ' + item.supplierBook.title;
                    } else if (item.book && item.book.bookName) {
                        bookName = item.book.bookName;
                    } else {
                        bookName = item.bookName || item.bookId || '未知书籍';
                    }
                    var total = (unitPrice * qty).toFixed(2);
                    html += '<tr data-poitemid="'+(item.poItemId||'')+'">';
                    html += '<td>' + (item.bookId||'') + '</td>';
                    html += '<td>' + bookName + '</td>';
                    html += '<td>' + unitPrice + '</td>';
                    html += '<td>' + qty + '</td>';
                    html += '<td>' + total + '</td>';
                    if(remaining > 0){
                        html += '<td><button class="layui-btn layui-btn-sm receive-btn" data-poitemid="'+(item.poItemId||'')+'">确认收货 ('+ remaining +')</button></td>';
                    } else {
                        html += '<td><span class="layui-badge layui-bg-green">已收齐</span></td>';
                    }
                    html += '</tr>';
                });
                html += '</tbody></table></div>';

                var layerIndex = layer.open({
                    type: 1,
                    area: ['820px','460px'],
                    title: '采购单 #' + (po.poId || data.poId) + ' 明细',
                    content: html,
                    success: function(layero, index){
                        var $content = layero.find('.layui-layer-content');
                        // bind receive buttons inside modal
                        $content.find('.receive-btn').on('click', function(){
                            var poItemId = $(this).data('poitemid');
                            var $btn = $(this);
                            $btn.prop('disabled', true);
                            $.post('/purchase/receive?poItemId=' + poItemId, function(r){
                                if(r.code!=0){
                                    layer.msg(r.msg,{icon:2});
                                    $btn.prop('disabled', false);
                                    return;
                                }
                                layer.msg('收货成功',{icon:1}, function(){
                                    layer.close(index);
                                    poTb.reload();
                                });
                            }, 'json').fail(function(){
                                layer.msg('请求失败',{icon:2});
                                $btn.prop('disabled', false);
                            });
                        });
                    }
                });
            });
        }
    });

    // 新建采购单 - 表单方式（供应商下拉 + 多项明细）
    $("#new-po-btn").on('click', function(){
        // load suppliers and books first
        $.getJSON('/supplier/list', function(sres){
            if(sres.code != 0) return layer.msg('获取供应商失败',{icon:2});
            $.getJSON('/book/searchcode?page=1&limit=1000', function(bres){
                if(bres.code != 0) return layer.msg('获取书籍失败',{icon:2});

                var supOptions = '<option value="">-- 选择供应商 --</option>';
                layui.each(sres.data || sres, function(i,s){
                    supOptions += '<option value="'+s.supplierId+'">'+s.name+'</option>';
                });

                var bookOptions = '<option value="">-- 选择书籍 --</option>';
                var outOfStockBooks = [];
                var inStockBooks = [];

                // 分离缺书和有货书籍
                layui.each(bres.data || bres, function(i,b){
                    if ((b.stock || 0) === 0) {
                        outOfStockBooks.push(b);
                    } else {
                        inStockBooks.push(b);
                    }
                });

                // 优先显示缺书书籍
                layui.each(outOfStockBooks, function(i,b){
                    bookOptions += '<option value="'+b.bookId+'" data-price="'+(b.price||0)+'" style="color: #ff5722; font-weight: bold;">[缺书] '+b.bookName+'（¥'+(b.price||0)+'）</option>';
                });

                // 然后显示有货书籍
                layui.each(inStockBooks, function(i,b){
                    bookOptions += '<option value="'+b.bookId+'" data-price="'+(b.price||0)+'">'+b.bookName+'（¥'+(b.price||0)+'）</option>';
                });

                // 存储供应商书目数据
                var supplierBooksData = {};

                var content = '<div style="padding:16px; max-height:420px; overflow:auto;">' +
                    '<div class="layui-form-item"><label class="layui-form-label">供应商</label>' +
                    '<div class="layui-input-block"><select id="po-supplier" class="layui-input">'+supOptions+'</select></div></div>' +
                    '<div style="margin:8px 0;"><button id="add-po-item" class="layui-btn layui-btn-sm">添加明细</button></div>' +
                    '<table class="layui-table" id="po-items-table" style="width:100%"><thead><tr><th>书籍</th><th>单价(元)</th><th>数量</th><th>操作</th></tr></thead><tbody></tbody></table>' +
                    '<div style="text-align:right;margin-top:12px;"><button id="po-create" class="layui-btn layui-btn-sm">创建采购单</button></div>' +
                    '</div>';

                var layerIndex = layer.open({
                    type:1,
                    area:['840px','520px'],
                    title:'新建采购单',
                    content: content,
                    success: function(layero, index){
                        // render form selects
                        var $content = layero.find('.layui-layer-content');
                        $content.find('#po-supplier').val('');

                        // 供应商选择事件
                        $content.find('#po-supplier').on('change', function(){
                            var supplierId = $(this).val();
                            if(supplierId){
                                loadSupplierBooks(supplierId, $content, bookOptions);
                            }
                        });
                        // helper to add a row
                        function addRow(){
                            var $row = $('<tr>');
                            var $bookSel = $('<select class="layui-input book-select">'+bookOptions+'</select>');
                            var $priceInput = $('<input type="text" readonly class="layui-input unit-price" style="width:120px;">');
                            var $qtyInput = $('<input type="number" min="1" value="1" class="layui-input qty-input" style="width:100px;">');
                            var $del = $('<button class="layui-btn layui-btn-danger layui-btn-sm">删除</button>');
                            $row.append($('<td>').append($bookSel));
                            $row.append($('<td>').append($priceInput));
                            $row.append($('<td>').append($qtyInput));
                            $row.append($('<td>').append($del));
                            $content.find('#po-items-table tbody').append($row);

                            // when book changes, fill price
                            $bookSel.on('change', function(){
                                var selectedOption = $(this).find('option:selected');
                                var price = selectedOption.data('price') || 0;
                                var supplyPrice = selectedOption.data('supplyprice') || price;
                                $priceInput.val(supplyPrice); // 优先使用供货价
                            });
                            $del.on('click', function(){
                                $row.remove();
                            });
                        }

                        // bind add item
                        $content.find('#add-po-item').on('click', function(e){
                            e.preventDefault();
                            addRow();
                        });

                        // pre-add one row
                        addRow();

                        // create submit
                        $content.find('#po-create').on('click', function(){
                            var supplierId = $content.find('#po-supplier').val();
                            if(!supplierId) return layer.msg('请选择供应商',{icon:2});
                            var items = [];
                            var valid = true;
                            $content.find('#po-items-table tbody tr').each(function(){
                                var bookValue = $(this).find('.book-select').val();
                                var unitPrice = parseFloat($(this).find('.unit-price').val() || 0);
                                var quantity = parseInt($(this).find('.qty-input').val() || 0);
                                if(!bookValue || quantity <= 0){
                                    valid = false;
                                    return false;
                                }

                                // 检查是否是供应商书目
                                var item = {};
                                if(bookValue.startsWith('supplier_')){
                                    // 供应商书目，需要特殊处理
                                    var supplierBookId = parseInt(bookValue.replace('supplier_', ''));
                                    item.supplierBookId = supplierBookId;
                                    item.isSupplierBook = true;
                                } else {
                                    // 普通书籍
                                    item.bookId = parseInt(bookValue);
                                    item.isSupplierBook = false;
                                }
                                item.quantity = quantity;
                                item.unitPrice = unitPrice;
                                items.push(item);
                            });
                            if(!valid || items.length===0) return layer.msg('请确认所有明细已填写且数量大于0',{icon:2});

                            var payload = {supplierId: parseInt(supplierId), items: items};
                            $.ajax({
                                url: '/purchase/create',
                                type: 'post',
                                data: JSON.stringify(payload),
                                contentType: 'application/json',
                                success: function(res){
                                    if(res.code!=0) return layer.msg(res.msg,{icon:2});
                                    layer.msg('创建成功 PO ID:' + res.data, {icon:1}, function(){ layer.close(index); poTb.reload(); });
                                }
                            });
                        });
                    }
                });
            });
        });
    });

    // 加载供应商书目
    function loadSupplierBooks(supplierId, $content, originalBookOptions) {
        $.getJSON('/supplier/book/list?supplierId=' + supplierId, function(res){
            if(res.code != 0) return layer.msg('获取供应商书目失败',{icon:2});

            // 更新所有书籍选择框的选项
            var enhancedBookOptions = originalBookOptions + '<optgroup label="供应商书目">';
            layui.each(res.data || [], function(i, book){
                if(book.status === 'ACTIVE'){
                    enhancedBookOptions += '<option value="supplier_' + book.supplierBookId + '" data-price="' + (book.price||0) + '" data-supplyprice="' + (book.supplyPrice||book.price||0) + '" style="background-color: #f0f8ff;">[供应商] ' + book.title + '（¥' + (book.supplyPrice||book.price||0) + '）</option>';
                }
            });
            enhancedBookOptions += '</optgroup>';

            // 更新所有现有的书籍选择框
            $content.find('.book-select').each(function(){
                var currentValue = $(this).val();
                $(this).html(enhancedBookOptions);
                if(currentValue) $(this).val(currentValue);
            });

            // 存储供应商书目数据以供后续使用
            supplierBooksData[supplierId] = res.data || [];
        });
    }

});


