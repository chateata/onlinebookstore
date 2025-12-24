layui.use(['table', 'form', 'jquery', 'layer', 'element'], function() {
	var table = layui.table,
		$ = layui.jquery,
		layer = layui.layer,
		element =layui.element,
		form = layui.form;

	var user_tb = table.render({
		elem: '#user_tb',
		url: '/user/list',
		cols: [
			[{
				field: 'userId',
				title: '网上ID',
				fixed: 'left',
				sort: true,
				width: 100
			}, {
				field: 'userName',
				title: '名称',
				width: 120
			}, {
				field: 'password',
				title: '登录密码',
				width: 120,
				templet: function(res) {
					return '******';
				}
			}, {
				field: 'email',
				title: '邮箱',
				width: 150,
				templet: function(res) {
					return '<em>' + res.email + '</em>';
				}
			}, {
				field: 'accountBalance',
				title: '账户余额',
				width: 100,
				templet: function(res) {
					return '¥' + (res.accountBalance || 0).toFixed(2);
				}
			}, {
				field: 'creditLevelName',
				title: '信用等级',
				width: 100,
				templet: function(res) {
					return res.creditLevel ? res.creditLevel.levelName : '未设置';
				}
			}, {
				field: 'overdraftLimit',
				title: '信用额度',
				width: 100,
				templet: function(res) {
					return res.creditLevel ? '¥' + res.creditLevel.overdraftLimit : '¥0.00';
				}
			}, {
				field: 'joinTime',
				title: '注册时间',
				width: 120
			}, {
				fixed: 'right',
				title: '操作',
				toolbar: '#barDemo',
				width: 150
			}]
		],
		page: true,
		height: 500
	});

	


	//监听行工具事件
	table.on('tool(user_tb)', function(obj) {
		var data = obj.data;
		if (obj.event === 'del') {
			layer.confirm('真的删除行么', {
				icon: 3
			}, function(index) {
				$.ajax({
					url: '/user/list/'+data.userId,
					type: 'delete',
					dataType: 'json',
					success: function(res) {
						if (res.code != 0) {
							return layer.msg("删除失败：" + res.msg, {
								icon: 2
							});
						}
						return layer.msg("删除成功", {
							icon: 1,
							time: 1300
						}, function() {
							obj.del();
						});
					}
				});
				layer.close(index);
			});
		} else if (obj.event === 'edit') {
			layer.open({
				type: 1,
				title: '编辑用户',
				content: $("#user_form_tmpl").html(),
				area: ['400px'],
				btn: ['更新'],
				yes: function(index1) {
					let form_data = form.val("user-form");
					let update_data = {
						userId: form_data.userId,
						userName: form_data.userName,
						password: form_data.password,
						email: form_data.email
					};

					// 处理余额调整
					if (form_data.balanceAdjustment !== undefined && form_data.balanceAdjustment !== null && form_data.balanceAdjustment !== "") {
						let adjustment = parseFloat(form_data.balanceAdjustment);
						let currentBalance = parseFloat((data.accountBalance || 0).toFixed(2));
						update_data.accountBalance = currentBalance + adjustment;
						if (update_data.accountBalance < 0) {
							return layer.msg("调整后余额不能为负数", {icon: 2});
					}
					} else {
						update_data.accountBalance = data.accountBalance || 0;
					}

					// 处理信用等级
					if (form_data.creditLevelId !== undefined && form_data.creditLevelId !== null && form_data.creditLevelId !== "") {
						update_data.creditLevelId = parseInt(form_data.creditLevelId);
					}

					console.log(update_data);
					$.ajax({
						url: '/user/update',
						type: 'post',
						data: JSON.stringify(update_data),
						contentType: 'application/json',
						dataType: 'json',
						success: function(res) {
							if (res.code != 0) {
								return layer.msg(res.msg, {
									icon: 2
								});
							}
							return layer.msg("更新成功", {
								icon: 1,
								time: 1300
							}, function() {
								// 刷新表格数据
								user_tb.reload();
								layer.close(index1);
							});
						}
					});
				},
				success: function() {
					//填充表单（编辑状态）
					form.val("user-form", data);
					// 显示当前余额
					$("#currentBalance").val('¥' + (data.accountBalance || 0).toFixed(2));
					// 显示当前信用额度
					$("#currentLimit").val(data.creditLevel ? '¥' + data.creditLevel.overdraftLimit : '¥0.00');

					// 加载信用等级列表
					$.getJSON("/creditLevel/list", function(res) {
						if (res.code == 0) {
							var options = '<option value="">请选择信用等级</option>';
							$.each(res.data, function(index, item) {
								var selected = data.creditLevelId == item.levelId ? 'selected' : '';
								options += '<option value="' + item.levelId + '" ' + selected + '>' +
									item.levelName + ' (折扣:' + item.discountRate + ', 额度:¥' + item.overdraftLimit + ')</option>';
							});
							$("#creditLevelSelect").html(options);
							form.render('select');
						}
					});
				}
			});
		}
	});

	
	//搜索
	var user_tb_this;
	form.on('submit(search_btn)', function(data) {
		if (user_tb_this != null) {
			user_tb_this.where = {};
		}
		user_tb.reload({
			url: '/user/search',
			where: data.field,
			page: {
				curr: 1
			},
			done: function() {
				user_tb_this = this;
			}
		});
		return false;
	});

});
