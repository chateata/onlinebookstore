layui.use(['table', 'form', 'layer'], function() {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;

    // 管理员表格
    var admin_tb = table.render({
        elem: '#admin_tb',
        url: '/admin/list',
        cols: [[
            {field: 'adminName', title: '管理员账号', width: 200},
            {field: 'password', title: '密码', width: 200, templet: function(d) {
                return '******'; // 隐藏密码显示
            }},
            {fixed: 'right', title: '操作', toolbar: '#barDemo', width: 150}
        ]],
        page: true,
        limit: 10,
        limits: [5, 10, 20, 50]
    });

    // 添加管理员按钮
    $('#addAdminBtn').on('click', function() {
        editAdmin(null);
    });

    // 工具条事件
    table.on('tool(admin_tb)', function(obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            editAdmin(data);
        } else if (obj.event === 'del') {
            layer.confirm('确认删除管理员 "' + data.adminName + '" 吗？', function(index) {
                $.ajax({
                    url: '/admin/' + data.adminName,
                    type: 'delete',
                    success: function(res) {
                        if (res.code != 0) {
                            return layer.msg(res.msg, {icon: 2});
                        }
                        layer.msg('删除成功', {icon: 1});
                        admin_tb.reload();
                    },
                    error: function() {
                        layer.msg('删除失败', {icon: 2});
                    }
                });
                layer.close(index);
            });
        }
    });

    // 编辑管理员函数
    function editAdmin(data) {
        var title = data ? '编辑管理员' : '添加管理员';
        var isEdit = !!data;

        layer.open({
            type: 1,
            title: title,
            content: $("#admin_form_tmpl").html(),
            area: ['400px'],
            btn: ['保存'],
            yes: function(index, layero) {
                var formData = form.val("admin-form");

                if (!formData.adminName || !formData.password) {
                    return layer.msg('请填写完整信息', {icon: 2});
                }

                var url = isEdit ? '/admin/update' : '/admin/insert';
                var method = isEdit ? 'put' : 'post';

                $.ajax({
                    url: url,
                    type: method,
                    data: JSON.stringify(formData),
                    contentType: 'application/json',
                    success: function(res) {
                        if (res.code != 0) {
                            return layer.msg(res.msg, {icon: 2});
                        }
                        layer.msg(isEdit ? '更新成功' : '添加成功', {icon: 1});
                        admin_tb.reload();
                        layer.close(index);
                    },
                    error: function() {
                        layer.msg(isEdit ? '更新失败' : '添加失败', {icon: 2});
                    }
                });
            },
            success: function(layero, index) {
                form.render(null, 'admin-form');
                if (isEdit) {
                    form.val('admin-form', data);
                }
            }
        });
    }
});
