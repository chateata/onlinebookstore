layui.use(['table', 'form', 'jquery', 'layer', 'upload','element'], function() {
	var table = layui.table,
		$ = layui.jquery,
		layer = layui.layer,
		upload = layui.upload,
		element =layui.element,
		form = layui.form;

	var book_tb = table.render({
		elem: '#book_tb',
		url: '/book/searchcode',
		cols: [
			[{
				field: 'bookId',
				title: 'ID',
				width: 110,
				fixed: 'left',
				sort: true
			}, {
				field: 'bookName',
				title: '书名',
				width: 120
			}, {
				field: 'categoryName',
				title: '分类',
				width: 80,
				templet: function(res) {
					return '<span class="layui-badge-rim">' + res.category.categoryName + '</span>';
				}
			}, {
				field: 'seriesName',
				title: '丛书',
				width: 120,
				templet: function(res) {
					return res.series ? res.series.seriesName : '不属于任何丛书';
				}
			}, {
				field: 'description',
				title: '简介',
				width: 120
			}, {
				field: 'isbn',
				title: 'ISBN',
				width: 120,
				templet: function(res) {
					return '<em>' + res.isbn + '</em>'
				}
			}, {
				field: 'press',
				title: '出版社',
				width: 120
			}, {
				field: 'author',
				title: '作者',
				width: 100
			}, {
				field: 'pubDate',
				title: '出版日期',
				width: 120
			}, {
				field: 'price',
				title: '价格',
				width: 80
			},  {
				field: 'stock',
				title: '库存',
				width: 80
			}, {
				field: 'shortageStatus',
				title: '缺书状态',
				width: 100,
				templet: function(res) {
					if (res.stock === 0) {
						return '<span class="layui-badge layui-bg-orange">缺书</span>';
					} else {
						return '<span class="layui-badge layui-bg-green">有货</span>';
					}
				}
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
	table.on('tool(book_tb)', function(obj) {
		var data = obj.data;
		if (obj.event === 'del') {
			layer.confirm('真的删除行么', {
				icon: 3
			}, function(index) {
				$.ajax({
					url: '/book/delete',
					type: 'post',
					data: {bookId:data.bookId},
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
				title: '编辑书籍',
				content: $("#book_form_tmpl").html(),
				area: ['500px'],
				btn: ['更新'],
				yes: function(index1) {
					let formData = form.val("book-form");

					// 处理丛书选择
					if(formData.seriesId === 'none') {
						formData.seriesId = null;
					} else if(formData.seriesId === 'new') {
						// 创建新丛书
						var newSeriesName = $('input[name="newSeriesName"]').val();
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
						url: '/book/update',
						type: 'post',
						data: formData,
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
								book_tb.reload(); // 重新加载表格数据
								layer.close(index1);
							});
						}
					});
				},
				success: function() {
					form.render(null, "book-form");
					$("#bookId").attr("disabled", true);
					$.getJSON("/category/searchall", function(res) {
						if (res.code != 0) {
							return;
						}
						$.each(res.data, function(index, item) {
							$("#categoryCode").append('<option value="' + item.categoryCode + '">' + item.categoryName +
								'</option> ');
						});

                        // 加载丛书数据
                        $.getJSON("/series/list", function(seriesRes) {
                            if (seriesRes.code == 0) {
                                var seriesOptions = '<option value="">请选择丛书</option>';
                                seriesOptions += '<option value="none">不属于任何丛书</option>';
                                seriesOptions += '<option value="new">新建丛书系列</option>';
                                $.each(seriesRes.data || [], function(index, item) {
                                    seriesOptions += '<option value="' + item.seriesId + '">' + item.seriesName + '</option>';
                                });
                                $("#seriesSelect").html(seriesOptions);
                            }

                            //填充表单（编辑状态）
                            form.val("book-form", data);
                            form.render();

                            // 丛书选择事件绑定（在form.render()之后）
                            form.on('select(seriesId)', function(seriesData){
                                console.log('Edit book - Series selection changed to:', seriesData.value); // 调试日志
                                if(seriesData.value === 'new') {
                                    console.log('Showing new edit series input'); // 调试日志
                                    $('#newEditSeriesInput').css('display', 'block');
                                } else {
                                    console.log('Hiding new edit series input'); // 调试日志
                                    $('#newEditSeriesInput').css('display', 'none');
                                    $('input[name="newSeriesName"]').val('');
                                }
                            });
                        });
					});
				}
			});
		}
	});


	//搜索
	var book_tb_this;
	form.on('submit(search_btn)', function(data) {
		if (book_tb_this != null) {
			book_tb_this.where = {};
		}

		// 处理库存筛选
		var searchData = $.extend({}, data.field);
		if (searchData.stockFilter) {
			if (searchData.stockFilter === 'out_of_stock') {
				searchData.stock = 0; // 只查询库存为0的书籍
			} else if (searchData.stockFilter === 'in_stock') {
				searchData.minStock = 1; // 查询库存大于0的书籍
			}
			delete searchData.stockFilter; // 删除前端筛选字段，后端不需要
		}

		book_tb.reload({
			url: '/book/search',
			where: searchData,
			page: {
				curr: 1
			},
			done: function() {
				book_tb_this = this;
			}
		});
		return false;
	});

});
