/**
 * 主页JS
 */
var scsIndexDataUrl = '244AyARAXAQA6ApAUAXA18oARATARA6ApAUAXA1ALAWAxAW', scsCurrentPage = 1, scsPageSize = 15,
serviceSelectType = '', serviceSelectDistinct = '', scsLng = 0,scsLat = 0;

$(function() {
	//获取当前城市名
	var map, geolocation;//= new AMap.Map("aMapDiv")
	//加载地图，调用浏览器定位服务
	map = new AMap.Map('aMapDiv', {
	    resizeEnable: true
	});
    $('.close-login').click(function() {
		$('#middleMain').hide();
		$('.page-mask').hide();
	});
	//实例化城市查询类
	var citysearch = new AMap.CitySearch();
	//自动获取用户IP，返回当前城市
	citysearch.getLocalCity(function(status, result) {
	    if (status === 'complete' && result.info === 'OK') {
	        if (result && result.city && result.bounds) {
	        	currentCityName = result.city;
	            //将上一次的城市名跟当前城市名相比较获取城市编号
	            last_city_name = localStorage.getItem(LAST_CITY_NAME_KEY);
	            if (last_city_name == currentCityName) {
	            	currentCityId = localStorage.getItem(LAST_CITY_ID_KEY);
				}else {
					currentCityId = '';
					localStorage.setItem(LAST_CITY_ID_KEY, '');
		            localStorage.setItem(LAST_CITY_NAME_KEY, currentCityName);
				}
	        }
	    } else {
	    	setCurrentCity();
	    }
	    queryScsIndexData();//请求主页页面数据
	});
	
	$('.service-title').click(function() {
		window.location.href = SCS_INDEX_URL + 'userIndex/showOrderDetail?oid=' + this.id;
	});
	$('.search-button').click(function() {
		scsCurrentPage = 1;
		queryIndexServiceOrderData(scsCurrentPage, scsPageSize);
	});
	//浏览器定位
    map.plugin('AMap.Geolocation', function() {
        geolocation = new AMap.Geolocation({
            enableHighAccuracy: true,//是否使用高精度定位，默认:true
            timeout: 20000,          //超过20秒后停止定位，默认：无穷大
            buttonOffset: new AMap.Pixel(10,30),//定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
            zoomToAccuracy: true,      //定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
            buttonPosition:'RB'
        });
        //map.addControl(geolocation);
        geolocation.getCurrentPosition();
        AMap.event.addListener(geolocation, 'complete', onComplete);//返回定位信息
        AMap.event.addListener(geolocation, 'error', onError);      //返回定位出错信息
    });
});
//定位失败的情况下，设置当前城市信息
function setCurrentCity(){
	last_city_name = localStorage.getItem(LAST_CITY_NAME_KEY);
	//如果上次保存的城市信息不存在，就将默认的城市信息赋值给当前城市
	if (last_city_name == null || last_city_name == '') {
		currentCityId = defaultCityId;
		defaultCityName = defaultCityName;
	}else {
		currentCityId = localStorage.getItem(LAST_CITY_ID_KEY);
		currentCityName = localStorage.getItem(LAST_CITY_NAME_KEY);
	}
}
//请求主页页面数据
function queryScsIndexData() {
	//初始化页面信息
	$.ajax({
		type: 'post',
		url: 'userIndex/initPageInfo',//_encrypt_decrypt.scs_decrypt(scsIndexDataUrl),//
		dataType: 'json',
		async: false,
		timeout: 30000,
		data: {cityId: currentCityId, cityName: currentCityName},
		success: function(result) {
			if (result.status == '0') {
				localStorage.setItem(LAST_CITY_ID_KEY, result.cityid);
				localStorage.setItem(LAST_CITY_NAME_KEY, currentCityName);
				$('#myCity').html(currentCityName);
				$('#myCity').attr('name', result.cityid);
				var areaArray = result.areaList;
				var appendStr = '';
				//添加服务区域信息
				for (var i = 0; i < areaArray.length; i++) {
					appendStr += '<a id="' + areaArray[i].areaid + '">' + areaArray[i].area + '</a>';
				}
				localStorage.setItem(ALL_AREA_KEY, areaArray);
				$('#serviceDistance').append(appendStr);
				appendStr = '';
				//添加服务类型信息
				var serviceTypeArray = result.serviceTypeList;
				var stKey = '', stValue = '';
				//sessionStorage.setItem(SERVICE_TYPE_LIST, serviceTypeArray);
				for (var j = 0; j < serviceTypeArray.length; j++) {
					var st = serviceTypeArray[j];
					stKey += st.P_BM + ',';
					stValue += st.NAME + ',';
					appendStr += '<a id="' + st.P_BM + '">' + st.NAME + '</a>';
				}
				sessionStorage.setItem(SERVICE_TYPE_KEY_LIST, stKey.substring(0,stKey.length - 1));
				sessionStorage.setItem(SERVICE_TYPE_VALUE_LIST, stValue.substring(0,stValue.length - 1));
				$('#serviceType').append(appendStr);
				
				//服务类型监听
				$('.service-select-type a').click(function() {
					var mySelectType = this;
					serviceSelectType = mySelectType.id == '0' ? '' : mySelectType.id;
					$('.service-select-type a').each(function() {
						if (mySelectType.id == this.id) {
							$(this).addClass('default-type');
						}else {
							$(this).removeClass('default-type');
						}
					});
					queryIndexServiceOrderData(scsCurrentPage, scsPageSize);
				});
				//区域监听
				$('.service-select-distinct a').click(function() {
					var mySelectType = this;
					serviceSelectDistinct = mySelectType.id == '0' ? '' : mySelectType.id;
					$('.service-select-distinct a').each(function() {
						if (mySelectType.id == this.id) {
							$(this).addClass('default-type');
						}else {
							$(this).removeClass('default-type');
						}
					});
					queryIndexServiceOrderData(scsCurrentPage, scsPageSize);
				});
			}
		}
	});
	scsCurrentPage = 1;
	queryIndexServiceOrderData(scsCurrentPage, scsPageSize);
}
function queryIndexServiceOrderData(currentPage, pageSize) {
	$.ajax({
		type: 'post',
		url: 'userIndex/queryServiceOrder',
		dataType: 'json',
		async: false,
		timeout: 30000,
		data: {
			cityId: currentCityId, 
			cityName: currentCityName,
			'currentPage': currentPage,
			'showCount':pageSize,
			so_areaid: serviceSelectDistinct,
			so_service_type: serviceSelectType,
			so_name: $('#searchServiceTitle').val()
		},
		success: function(result) {
			if (result.status == '0') {
				//服务订单数据
				var appendStr = '';
				var serviceOrderArray = result.serviceOrderList;
				for (var s = 0; s < serviceOrderArray.length; s++) {
					var order = serviceOrderArray[s];
					var phone = order.so_phone;//.substring(0,3);
					//phone = phone + '****' + order.so_phone.substring(7,order.so_phone.length);
					var province = order.province;
					var city = order.city;
					if (province != city) {
						city = province + city;
					}
					var yourDistance = '天涯海角';
					if (!isNull(order.so_lat) && !isNull(order.so_lng)) {
						if (scsLat > 0 && scsLng > 0) {
							yourDistance = getDistanceByLatLng(getLat(),getLng(),Number(order.so_lat),Number(order.so_lng));
						}
					}
					
					if (yourDistance > 1000) 
						yourDistance = (yourDistance / 1000).toFixed(2) + '&nbsp;Km';
					else if (yourDistance != '天涯海角') 
						yourDistance = parseInt(yourDistance) + '&nbsp;m';
					
					appendStr += '<div id="serviceInfo' + s + '" class="service-info">' +
						'<input type="hidden" id="issuerUserId_'+order.so_bill_number+'" value="'+order.so_user_id+'"/>' +
				        '<img class="service-png" src="static/images/userIndex/logo.png" alt="' + order.so_name + '" />' +
				            '<ul>' +
				                '<strong class="service-title"><a target="_blank" href="' + SCS_INDEX_URL + 'userIndex/showOrderDetail?oid=' + order.so_bill_number + '">' + order.so_name + '</a></strong>' +
				                '<li>' +
				                 '   发布时间：' + order.so_order_date + ' &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
				                 '   服务类型：'+order.service_type_name+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;金额：<strong>' + order.so_amount + '</strong>&nbsp;元' +
				                '</li>' +
				                '<li>' +
				                 '   服务时间：' + order.so_service_date + ' &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
				                 '   距&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您：'+yourDistance+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;电话：' + phone +
				                '</li>' +
				                '<li>服务地址：' + city + order.area + order.so_address + '</li>' +
				            '</ul>' +
				            '<span class="grab-order">' +
				                '<button id="btnGrabOrder' + s + '" onclick="robServiceOrder(\''+order.so_bill_number+'\','+s+')" class="btn-grab-order">我要抢单</button>' +
				            '</span>' +
				        '</div>';
				}
				$('#serviceList').children().remove();
				$('#serviceList').append(appendStr);
				$('#serviceList').show();
				
				//添加分页工具栏
				$('.wp-paginate').children().remove();
				var serviceOrderTotalResult = parseInt(result.serviceOrderTotalResult);//总记录数
				var serviceOrderTotalPage = parseInt(result.serviceOrderTotalPage);//总页数
				var previousPageCss = ' nohave';
				if (currentPage > 1) {
					previousPageCss = ' have';
				}
				appendStr = '<li><a href="javascript:void(0)" class="first'+previousPageCss+'">首页</a></li>'+
			        '<li><a href="javascript:void(0)" class="previous'+previousPageCss+'">&lt;</a></li>';
				if (serviceOrderTotalPage > 0) {
					////页面要显示的页数集合
					var pagingNumber = serviceOrderTotalPage > 10 ? 10 : serviceOrderTotalPage;
					var k = 0;
					//保证当前页为中间页
					for (var g = 0; g < serviceOrderTotalResult; g++) {
						var currentPageCss = (g + 1) == currentPage ? ' current' : ' have';
						if((g >= currentPage - (pagingNumber / 2 + 1) || g >= serviceOrderTotalPage - pagingNumber) && k < pagingNumber){
							appendStr += '<li><a href="javascript:void(0)" class="page'+currentPageCss+'">'+(g+1)+'</a></li>';
							k ++;
						}else if (k >= pagingNumber) {
							break;
						}
					}
				}
				previousPageCss = ' have';
				if (currentPage >= serviceOrderTotalPage) {
					previousPageCss = ' nohave';
				}
				appendStr += '<li><a href="javascript:void(0)" class="next'+previousPageCss+'">&gt;</a></li>'+
							'<li><a href="javascript:void(0)" class="last'+previousPageCss+'">尾页</a></li>';
				$('.wp-paginate').append(appendStr);
			}
			$(".wp-paginate .nohave").hover(function(){
		        $(this).css({"cursor":"default"});
		    });
			$(".wp-paginate .have").mouseover(function(){
		        $(this).css({'border':'2px #d7ab75 solid','color':'#262626'});
		    }).mouseout(function() {
				$(this).css({'border':'2px #e3e3e3 solid','color':'#858585'});
			});
			$(".wp-paginate a").click(function() {
				var className = this.className;
				if (className.indexOf('nohave') == -1 && parseInt(this.innerText) != currentPage) {
					if(className.indexOf('first') > -1)
						currentPage = 1;
					else if(className.indexOf('previous') > -1)
						currentPage = currentPage > 1 ? currentPage - 1 : 1;
					else if(className.indexOf('next') > -1)
						currentPage = currentPage < serviceOrderTotalPage ? currentPage + 1 : serviceOrderTotalPage;
					else if(className.indexOf('last') > -1)
						currentPage = serviceOrderTotalPage;
					else 
						currentPage = parseInt(this.innerText);
					queryIndexServiceOrderData(currentPage, pageSize);
				}
			});
			var footerBottom = $('body').height() - $('#footer').offset().top - 154;
			if (footerBottom > 2) {
				$('#footer').css({'bottom':'0','position':'fixed'});
			}
		}
	});
}
//解析定位结果
function onComplete(data) {
	scsLng = data.position.getLng(), scsLat = data.position.getLat();
	localStorage.setItem(LAST_LNG, scsLng);//经度
	localStorage.setItem(LAST_LAT, scsLat);//纬度
	sessionStorage.setItem(LAST_LNG, scsLng);
	sessionStorage.setItem(LAST_LAT, scsLat);
	//alert(scsLng + ',' + scsLat);
}
//解析定位错误信息
function onError(data) {
	getLat();
	getLng();
}
function getLat() {
	if (isNull(scsLat) || scsLat < 0) {
		//从sessionStorage和localStorage中获取经纬度信息
		scsLat = sessionStorage.getItem(LAST_LAT);
		if (isNull(scsLat) || scsLat <= 0) 
			scsLat = localStorage.getItem(LAST_LAT);
	}
	return scsLat;
}
function getLng() {
	if (isNull(scsLng) && scsLng <= 0) {
		//从sessionStorage和localStorage中获取经纬度信息
		scsLng = sessionStorage.getItem(LAST_LNG);
		if (isNull(scsLng) || scsLng <= 0) 
			scsLng = localStorage.getItem(LAST_LNG);
	}
	return scsLng;
}