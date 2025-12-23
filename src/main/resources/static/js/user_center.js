layui.use(['jquery','layer'], function () {
    var $ = layui.jquery;
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


