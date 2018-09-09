/**
 * 主页JS
 */
var bodyWidth = 1140, start = 0, totalPage = 0, totalResult = 0, currentPage = 1, pageSize = 12;
$(function() {
    $('.service-png').click(function(){
        location.href = 'xccm/customerDetail.html';
    });
    $('.service-title').click(function(){
        location.href = 'xccm/customerDetail.html';
    });
    var seeWidth = document.documentElement.clientWidth;
    $('.logo-word').css('margin-left', (seeWidth - bodyWidth) / 2 + 'px');
    
    //setPageTool();
	$('.search-button').click(function() {
		currentPage = 1;
		queryIndexData(1,pageSize);//请求主页页面数据
	});
	$('.first').click(function() {
		if (this.className.indexOf('current') > 0) {
			currentPage = 1;
			queryIndexData(1,pageSize);//请求主页页面数据
		}
	});
	$('.previous').click(function() {
		if (this.className.indexOf('current') > 0) {
			currentPage --;
			queryIndexData(currentPage,pageSize);//请求主页页面数据
		}
	});
	$('.next').click(function() {
		if (this.className.indexOf('current') > 0) {
			currentPage ++;
			queryIndexData(currentPage,pageSize);//请求主页页面数据
		}
	});
	$('.last').click(function() {
		if (this.className.indexOf('current') > 0) {
			currentPage = totalPage;
			queryIndexData(currentPage,pageSize);//请求主页页面数据
		}
	});
	$('.btn-go-page').click(function() {
		var errorMsg = '', goPage = $('#goPage').val();
		if (goPage == '') {
			errorMsg = '请输入要跳转的页数';
		}else if (isNaN(goPage) || goPage.indexOf('.') > -1) {
			errorMsg = '输入正确的正整数';
		}else if (parseInt(goPage) > totalPage) {
			errorMsg = '不能大于总页数';
		}else if (parseInt(goPage) < 1) {
			errorMsg = '不能小于1';
		}
		if (errorMsg != '') {
			$("#goPage").tips({
				side:3,
	            msg: errorMsg,
	            bg:'#d7ab75',
	            time:2
	        });
			$("#goPage").focus();
			return false;
		}else {
			currentPage = goPage;
			queryIndexData(currentPage,pageSize);//请求主页页面数据
		}
	});
	queryIndexData(1,pageSize);//请求主页页面数据
});

//查看详情
function seeCustmoerShop(id) {
	window.location.href = 'xccm/showCustomerDetail?id=' + id;
}
//请求主页页面数据
function queryIndexData(currentPage, pageSize) {
	var startCount = (currentPage - 1) * pageSize;
	var shopName = $('#shopName').val();
	if (shopName == '输入名称...') {
		shopName = '';
	}
	$.ajax({
		type: 'post',
		url: 'xccm/shopList',//'userIndex/scsIndexData',
		dataType: 'json',
		async: false,
		timeout: 30000,
		data: {
			shopName: shopName,
			'currentResult': startCount,
			'currentPage': currentPage,
			'showCount': pageSize
		},
		success: function(result) {
			if (result.status == '0') {
				$('#serviceList').empty();
				var appendStr = '<img class="weChat-QR-Code" src="static/xccm/images/weChat.png" />';
				//店铺数据
				var shopArray = result.shopList;
				if (shopArray.length < 1) {
					appendStr = '<p>暂时没查到客户信息</p>';
				}else {
					for (var g = 0; g < shopArray.length; g++) {
						var shop = shopArray[g];
						var className = 'service-three';
						if ((g +　1) % 3 == 1) {
							appendStr += '<div class="service-row">';
							className = 'service-one';
						}else if ((g +　1) % 3 == 2) {
							className = 'service-two';
						}
						var shopAddress = '';
						if (typeof(shop.address) !='undefined') {
							if (shop.address.length > 10) {
								shopAddress = shop.address;
								shopAddress = shopAddress.substring(0, 10) + '...';
							}else {
								shopAddress = shop.address;
							}
						}
						var headPicturePath = 'static/xccm/images/defaultHeader.png';
						if (shop.headpicture != '' && typeof(shop.headpicture) != 'undefined') {
							headPicturePath = '/headerPicture/'+shop.headpicture;
						}
						appendStr += '<div class="'+className+'">' +
						            '<div class="service-info">' +
						                '<img onclick="seeCustmoerShop(&quot;'+shop.id+'&quot;)" class="service-png" src="'+headPicturePath+'" alt="'+shop.name+'" />' +
						                '<ul>' +
						                    '<strong onclick="seeCustmoerShop(&quot;'+shop.id+'&quot;)" class="service-title">'+shop.name+'</strong>' +
						                    '<li>电话：'+(typeof(shop.phone) =='undefined' ? '':shop.phone)+'</li>' +
						                    '<li>地址：'+shopAddress+'</li>' +
						                '</ul>' +
						            '</div>' +
						        '</div>';
				        if ((g +　1) % 3 == 1) {
							appendStr += '</div>';
						}
					}
				}
				
				$('#serviceList').append(appendStr + '<br/>');
				$('#serviceList').show();
				start = parseInt(result.start),totalPage = parseInt(result.totalPage), 
				totalResult = parseInt(result.totalCount), currentPage = parseInt(result.currentPage);
				$('#totalPage').text(totalPage);
				$('#totalResult').text(totalResult);
				$('#currentPage').attr('value', currentPage);
				if (totalPage < 2) {
					$(".first").removeClass('current');
					$(".previous").removeClass('current');
					$(".next").removeClass('current');
					$(".last").removeClass('current');
				}else if (currentPage > 1 && currentPage < totalPage && totalPage > 1) {
					$(".first").addClass('current');
					$(".previous").addClass('current');
					$(".next").addClass('current');
					$(".last").addClass('current');
				}else if(currentPage < 2 && totalPage > 1){
					$(".first").removeClass('current');
					$(".previous").removeClass('current');
					$(".next").addClass('current');
					$(".last").addClass('current');
				}else {
					$(".first").addClass('current');
					$(".previous").addClass('current');
					$(".next").removeClass('current');
					$(".last").removeClass('current');
				}
				
				setPageHeight();
			}
		}
	});
}
//设置分页工具
/*function setPageTool() {
	$(".first").mouseover(function(){
		if(currentPage > 1)
			$(".first").addClass('current');
	});
	$(".first").mouseout(function(){
	    $(".first").removeClass('current');
	});
	$(".previous").mouseover(function(){
		if(currentPage > 1)
			$(".previous").addClass('current');
	});
	$(".previous").mouseout(function(){
	    $(".previous").removeClass('current');
	});
	$(".next").mouseover(function(){
		if(currentPage < totalPage)
			$(".next").addClass('current');
	});
	$(".next").mouseout(function(){
	    $(".next").removeClass('current');
	});
	$(".last").mouseover(function(){
		if(currentPage < totalPage)
			$(".last").addClass('current');
	});
	$(".last").mouseout(function(){
	    $(".last").removeClass('current');
	});
}*/