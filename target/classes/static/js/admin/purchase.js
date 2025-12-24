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
            {field:'expectedArrivalDate', title:'预计到货', width:160},
            {field:'status', title:'状态', width:120},
            {fixed:'right', title:'操作', toolbar:'#po_bar', width:140}
        ]],
        page:true,
        height: 480
    });

    table.on('tool(po_tb)', function(obj){
        var data = obj.data;
        if(obj.event === 'detail'){
            // fetch full PO details
            $.getJSON('/purchase/list?poId=' + data.poId, function(res){
                if(res.code != 0) return layer.msg(res.msg,{icon:2});
                var po = res.data;
                var html = '<table class="layui-table"><thead><tr><th>书ID</th><th>数量</th><th>已收</th><th>单价</th><th>操作</th></tr></thead><tbody>';
                layui.each(po.items || [], function(i,item){
                    html += '<tr><td>' + item.bookId + '</td><td>' + item.quantity + '</td><td>' + (item.receivedQuantity||0) + '</td><td>' + item.unitPrice + '</td>';
                    html += '<td><input type="number" id="r_' + item.poItemId + '" min="1" style="width:80px"/>&nbsp;<button class="layui-btn layui-btn-sm" onclick="receiveItem(' + item.poItemId + ')">收货</button></td></tr>';
                });
                html += '</tbody></table>';
                $("#po-detail").html(html);
                // expose receiveItem globally
                window.receiveItem = function(poItemId){
                    var added = parseInt($("#r_" + poItemId).val() || 0);
                    if(!added || added<=0) return layer.msg('请输入大于0的收货数量',{icon:2});
                    $.post('/purchase/receive?poItemId=' + poItemId + '&addedReceived=' + added, function(r){
                        if(r.code!=0) return layer.msg(r.msg,{icon:2});
                        layer.msg('收货成功',{icon:1}, function(){ $("#r_" + poItemId).val(''); poTb.reload(); });
                    }, 'json');
                };
            });
        }
    });

    // 新建采购单 - 简易弹窗（JSON 输入）
    $("#new-po-btn").on('click', function(){
        layer.open({
            type:1,
            area:['720px','480px'],
            title:'新建采购单（JSON 格式，items 为数组，每项含 bookId, quantity, unitPrice）',
            content: '<div style="padding:20px;"><textarea id="po-json" style="width:100%;height:320px;">{\n  "supplierId": 1,\n  "items": [\n    {\"bookId\":1001,\"quantity\":10,\"unitPrice\":45.00}\n  ]\n}</textarea><div style="text-align:right;margin-top:12px;"><button id="po-create" class="layui-btn layui-btn-sm">创建</button></div></div>',
            success: function(layero, index){
                $("#po-create").on('click', function(){
                    var raw = $("#po-json").val();
                    try {
                        var obj = JSON.parse(raw);
                    } catch(e){
                        return layer.msg('JSON 格式错误',{icon:2});
                    }
                    $.ajax({
                        url: '/purchase/create',
                        type: 'post',
                        data: JSON.stringify(obj),
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


