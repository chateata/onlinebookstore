layui.use(['jquery','layer','form'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form;

    // 请求用户基本信息
    $.getJSON("/user/info", function (res) {
        if (res.code != 0) {
            return;
        }
        var data = res.data;
        if (!data) return;
        $("#uc-username").text(data.userName || '--');
        $("#uc-password").text('********'); // 隐藏密码显示
        $("#uc-email").text(data.email || '--');
        $("#uc-join-time").text(data.joinTime ? new Date(data.joinTime).toLocaleDateString() : '--');
    });

    // 请求用户余额与信用等级信息
    $.getJSON("/user/credit", function (res) {
        console.debug("user_center.js /user/credit response:", res);
        if (res.code != 0) {
            return;
        }
        var data = res.data;
        if (!data) return;
        var balance = data.accountBalance;
        var cl = data.creditLevel;
        $("#uc-balance").text(balance == null ? '--' : balance);
        $("#uc-credit-level").text(cl ? cl.levelName : '--');
        $("#uc-discount").text(cl ? (cl.discountRate ? (cl.discountRate*100 + '%') : '--') : '--');
        $("#uc-overdraft").text(cl ? cl.overdraftLimit : '--');
    });

    // 修改密码按钮点击事件
    $("#changePasswordBtn").click(function() {
        layer.open({
            type: 1,
            title: '修改密码',
            content: $("#password-change-tpl").html(),
            area: ['400px'],
            btn: ['确认修改', '取消'],
            yes: function(index, layero) {
                var formData = form.val("passwordForm");
                if (!formData.oldPassword || !formData.newPassword || !formData.confirmPassword) {
                    layer.msg('请填写完整信息', {icon: 2});
                    return;
                }
                if (formData.newPassword !== formData.confirmPassword) {
                    layer.msg('两次输入的新密码不一致', {icon: 2});
                    return;
                }
                if (formData.newPassword.length < 6) {
                    layer.msg('新密码至少6个字符', {icon: 2});
                    return;
                }

                $.ajax({
                    url: '/user/changePassword',
                    type: 'post',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        oldPassword: formData.oldPassword,
                        newPassword: formData.newPassword
                    }),
                    success: function(res) {
                        if (res.code != 0) {
                            layer.msg(res.msg, {icon: 2});
                            return;
                        }
                        layer.msg('密码修改成功，请重新登录', {icon: 1, time: 2000}, function() {
                            // 清除session并跳转到登录页
                            window.location.href = '/logout';
                        });
                    },
                    error: function() {
                        layer.msg('修改失败，请稍后重试', {icon: 2});
                    }
                });
            }
        });
    });

    // 同步页头显示
    $.getJSON("/user/credit", function (res) {
        if (res.code != 0) return;
        var data = res.data;
        if (!data) return;
        $("#header-balance").text("余额：" + (data.accountBalance == null ? '--' : data.accountBalance));
        var cl = data.creditLevel;
        $("#header-credit").text("等级：" + (cl ? cl.levelName : '--'));
    });
});


