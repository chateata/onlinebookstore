layui.use(['element', 'jquery', 'layer', 'laytpl','laypage'], function() {
	var $ = layui.jquery,
		layer = layui.layer,
		laytpl = layui.laytpl,
		laypage=layui.laypage,
		element = layui.element;

	// 存储用户信息
	var currentUser = null;

	// 获取当前用户信息
	function getCurrentUser() {
		$.getJSON('/user/credit', function(res) {
			if (res.code == 0) {
				currentUser = res.data;
			}
		});
	}

	// 计算折后价格
	function calculateDiscountedPrice(originalPrice) {
		if (!currentUser || !currentUser.creditLevel || !currentUser.creditLevel.discountRate) {
			return originalPrice; // 没有用户信息或折扣率时返回原价
		}
		var discountRate = parseFloat(currentUser.creditLevel.discountRate);
		return (originalPrice * (1 - discountRate)).toFixed(2);
	}

	// 获取折扣率文本
	function getDiscountText() {
		if (!currentUser || !currentUser.creditLevel) {
			return '';
		}
		var discountRate = parseFloat(currentUser.creditLevel.discountRate);
		return (discountRate * 100) + '%折扣';
	}


	// 获取当前用户信息
	getCurrentUser();

	//请求加载分类信息，渲染选项卡
	$.getJSON("/index/category",function(res){
		if(res.code!=0){
			$("#content").html(res.msg);
			return ;
		}
		$.each(res.data,function(index,item){
			let str='<li c-code="'+item.categoryCode+'">'+item.categoryName+'</li>';
			$("#category_tag").append(str);
		});
		//请求全部书籍
		getBooksByPage('/index/books',{page:1,limit:10});
	});
	
	

	/**
	 * 监听切换分类选项卡的切换
	 */
		element.on('tab(categoryTabBrief)', function(data) {
		// console.log($(this).attr("c-code")); //当前Tab标题所在的原始DOM元素
		if(!($(this).attr('lay-id')==='search')){
			//获取分类代码
			let code=$(this).attr("c-code");
			getBooksByPage('/index/books',{page:1,limit:10,categoryCode:code});
		}else {
			// 切换到搜索标签时，如果有搜索内容则显示loading
			let keyword=$("#keyword-input").val().trim();
			if(keyword.length>0){
				let searchType=$("#search-type").val();
				getBooksByPage('/index/books/search',{page:1,limit:10,keyword:keyword,searchType:searchType});
			} else {
				$("#content").html('<div style="text-align: center;padding: 60px 20px;color: var(--muted);">请输入搜索关键词</div>');
			}
		}
	});
	
	//搜索
	$("#search-btn").click(function(){
		element.tabChange('categoryTabBrief', 'search');
		let keyword=$("#keyword-input").val().trim();
		if(keyword.length==0){
			return;
		}
		let searchType=$("#search-type").val();
		getBooksByPage('/index/books/search',{page:1,limit:10,keyword:keyword,searchType:searchType});
	});

	/**
	 * 请求分页查询
	 * @param url
	 * @param param
	 */
	function getBooksByPage(url,param) {
		param['page']=param.page||1;
		param['limit']=param.limit||10;

		// 显示loading动画
		$("#content").html('<div class="book-loading"><div class="loading-spinner"></div><div class="loading-text">正在加载书籍...</div></div>');

		$.getJSON(url,param,function (result) {
			if(result.code!=0){
				$("#content").html('<div style="text-align: center;font-size: 20px;">'+result.msg+'</div>');
				return ;
			}
			if(result.data.length==0){
				$("#content").html('<div style="text-align: center;font-size: 20px;">暂时没有数据</div>');
				return ;
			}
			// Get raw template text (prefer .text() to avoid HTML-escaping by Thymeleaf)
			var tplHtml = $("#book-card-tpl").text() || $("#book-card-tpl").html();
			try {
				tplHtml = $("<textarea/>").html(tplHtml).text();
			} catch (e) {}
			console.debug("index template before render:", tplHtml.slice(0,200));
			console.debug("index data sample:", (result.data && result.data.length>0) ? result.data[0] : result.data);
			try {
				// 准备渲染数据，包含用户信息
				var renderData = {
					data: result.data,
					userDiscountRate: currentUser && currentUser.creditLevel ? currentUser.creditLevel.discountRate : null,
					userDiscountText: getDiscountText(),
					calculateDiscountedPrice: calculateDiscountedPrice
				};
				laytpl(tplHtml).render(renderData,function(html){
					console.debug("index rendered html preview:", html.slice(0,200));
					$("#content").html('<div class="book-container"><div class="book-grid">' + html + '</div></div>');
				});
			} catch (e) {
				console.error("laytpl render error:", e);
				$("#content").text(tplHtml);
			}
			//调用分页
			laypage.render({
				elem: 'page-util'
				,count: result.count
				,curr: param.page||1
				,limit: param.limit||10
				,jump: function(obj,first){
					if(!first){
						// console.log(obj);
						param.page=obj.curr;
						param.limit=obj.limit;
						getBooksByPage(url, param);
					}
				}
			});
		})
	}



});
