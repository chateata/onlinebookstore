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
        }
    });

});


