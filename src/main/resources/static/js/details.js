layui.use(['element', 'jquery', 'layer', 'laytpl','laypage','form','table'], function() {
	var $ = layui.jquery,
		layer = layui.layer,
		laytpl = layui.laytpl,
		laypage=layui.laypage,
		form=layui.form,
		element = layui.element,
		table = layui.table;

		//从url路径中获取书籍ID
		var str=window.location.pathname;
		var bookId=str.substring(str.lastIndexOf("/")+1);

		// 获取当前用户信息并显示折扣价格
		getCurrentUserAndShowDiscount();

		// 存储用户信息
		var currentUser = null;

		// 获取当前用户信息并显示折扣价格
		function getCurrentUserAndShowDiscount() {
			$.getJSON('/user/credit', function(res) {
				if (res.code == 0) {
					currentUser = res.data;
					showDiscountedPrice();
				}
			});
		}

		// 显示折扣价格
		function showDiscountedPrice() {
			var originalPrice = parseFloat($("#price").text().replace('¥', ''));
			if (currentUser && currentUser.creditLevel && currentUser.creditLevel.discountRate) {
				var discountRate = parseFloat(currentUser.creditLevel.discountRate);
				var discountedPrice = (originalPrice * (1 - discountRate)).toFixed(2);

				// 修改价格显示
				$("#price").html(
					'<span class="original-price">¥' + originalPrice.toFixed(2) + '</span><br>' +
					'<span class="discount-text">' + (discountRate * 100) + '%折扣</span><br>' +
					'<span class="discounted-price">¥' + discountedPrice + '</span>'
				);

				// 更新合计价格
				updateTotalPrice(discountedPrice);
			}
		}

		// 更新合计价格
		function updateTotalPrice(unitPrice) {
			var quantity = parseInt($("#quantity").val()) || 1;
			var total = (unitPrice * quantity).toFixed(2);
			$("#totalAccount").text('¥' + total);
		}

		//立即购买提交按钮
		$("#buyNowBtn").click(function(){
			let data=[];
			let quantity=$("#quantity").val();
			if(quantity==0){
				return layer.msg('至少要一件才能购买哦',{icon:5});
			}

			// 计算实际价格（考虑折扣）
			var originalPrice = parseFloat($(".discounted-price").text().replace('¥', '')) ||
							   parseFloat($("#price").text().replace('¥', ''));
			if (isNaN(originalPrice)) {
				// 如果没有折扣价格，获取原始价格
				originalPrice = parseFloat($("#price").text().replace('¥', ''));
			}

			data.push({
				'cartId':null,
				'bookId':bookId,
				'price':originalPrice,
				'quantity':quantity
			});
			console.log(data);
			order_submit_popup(data);

		});
		
		//添加购物车提交按钮
		$("#addCartBtn").click(function(){
			let quantity=$("#quantity").val();
			if(quantity==0){
				return layer.msg('至少要购买一件才能添加购物车哦',{icon:5});
			}

			// 计算实际价格（考虑折扣）
			var actualPrice = parseFloat($(".discounted-price").text().replace('¥', '')) ||
							 parseFloat($("#price").text().replace('¥', ''));
			if (isNaN(actualPrice)) {
				// 如果没有折扣价格，获取原始价格
				actualPrice = parseFloat($("#price").text().replace('¥', ''));
			}

			let data={
				'bookId':bookId,
				'price':actualPrice,
				'quantity':quantity
			};
			$.post('/cart/list', data, function (res) {
				if (res.code != 0) {
					return layer.msg(res.msg,{icon:2});
				}
				return layer.msg("添加成功,请前往购物车查看",{icon:1});
			});
		});
		
		//购买数量输入框监听
		window.check=function(obj,price){
			var qty=$(obj).val();
			if(qty<0||qty==''){
				layer.msg("输入必须大于或者等于0");
				$(obj).val(0);
				qty=0;
			}
			if(qty>10){
				layer.msg("每个用户限购10件");
				$(obj).val(10);
				qty=10;
			}

			// 计算实际价格（考虑折扣）
			var actualPrice = price;
			if (currentUser && currentUser.creditLevel && currentUser.creditLevel.discountRate) {
				var discountRate = parseFloat(currentUser.creditLevel.discountRate);
				actualPrice = price * (1 - discountRate);
			}

			$("#totalAccount").text("¥"+Math.floor(parseFloat(actualPrice*100 *qty))/100);
		}
});