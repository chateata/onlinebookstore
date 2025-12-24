layui.use(['table', 'form', 'jquery', 'layer'], function() {
    var table = layui.table,
        $ = layui.jquery,
        layer = layui.layer,
        form = layui.form;

    var invTb = table.render({
        elem: '#inventory_tb',
        url: '/book/searchcode',
        cols: [[
            {field:'bookId', title:'ID', width:100, sort:true},
            {field:'bookName', title:'书名', width:260},
            {field:'isbn', title:'ISBN', width:160},
            {field:'press', title:'出版社', width:160},
            {field:'price', title:'价格', width:100},
            {field:'stock', title:'库存', width:100},
            {fixed:'right', title:'操作', toolbar:'#inv_bar', width:160}
        ]],
        page:true,
        height: 520
    });

    $("#refresh-inv").on('click', function(){ invTb.reload(); });

    table.on('tool(inventory_tb)', function(obj){
        var data = obj.data;
        if(obj.event === 'adjust'){
            layer.open({
                type:1,
                title:'调整库存 - ' + data.bookName,
                area:['420px','220px'],
                content: '<div style="padding:20px;"><div class="layui-form-item"><label class="layui-form-label">变更数量(+/-)</label><div class="layui-input-block"><input type="number" id="delta" class="layui-input" value="0"></div></div><div style="text-align:right;"><button id="do-adjust" class="layui-btn layui-btn-sm">提交</button></div></div>',
                success: function(layero, idx){
                    $("#do-adjust").on('click', function(){
                        var delta = parseInt($("#delta").val() || 0);
                        if(delta === 0) return layer.msg('请输入非0变更量',{icon:2});
                        $.post('/book/adjustStock', {bookId: data.bookId, delta: delta}, function(res){
                            if(res.code != 0) return layer.msg(res.msg,{icon:2});
                            layer.msg('调整成功',{icon:1}, function(){ layer.close(idx); invTb.reload(); });
                        }, 'json');
                    });
                }
            });
        } else if(obj.event === 'restock'){
            // 快速补货：弹窗提示输入补货数量并创建采购单跳转（简化：直接调整库存）
            layer.prompt({title: '补货数量（将直接增加库存）', formType:0}, function(val, index){
                var added = parseInt(val || 0);
                if(!added || added<=0) { layer.msg('请输入大于0的数量',{icon:2}); return; }
                $.post('/book/adjustStock', {bookId: data.bookId, delta: added}, function(res){
                    if(res.code != 0) return layer.msg(res.msg,{icon:2});
                    layer.msg('补货成功',{icon:1}, function(){ layer.close(index); invTb.reload(); });
                }, 'json');
            });
        }
    });

});


