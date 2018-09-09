/**
 * 发布服务JS
 */
/**
 * 验证手机号码是否正确，正确返回true
 * @param phoneNumber
 */
function phoneNumberIsRight(phoneNumber) {
    var reg = /^0?1[1-9][0-9]\d{8}$/;
    if (reg.test(phoneNumber)){
        return true;
    }
    return false;
}
var map,geolocation, mapMarker,clickLng,clickLat,placeSearch,serviceCity,cityArea;
$(function() {
	I('soPhone').value = sessionStorage.getItem(SCS_USER_PHONE);
	var stKey = sessionStorage.getItem(SERVICE_TYPE_KEY_LIST);
	if (isNull(stKey)) {
		queryAllServiceType();
	}else {
		var stValue = sessionStorage.getItem(SERVICE_TYPE_VALUE_LIST);
		var stKeyArray = stKey.split(',');
		var stValueArray = stValue.split(',');
		var stStr = '';
		for (var i = 0; i < stKeyArray.length; i++) {
			stStr += '<option value="' + stKeyArray[i] + '">' + stValueArray[i] + '</option>';
		}
		$('#serviceType').append(stStr);
	}
	//显示高德地图
	$('#soAddress').focus(function() {
		showAMap();
	});
	//关闭遮罩层和地图
	$('.close-mask-layer').click(function() {
		closeMaskLayerAndMap();
	});
	$('#btnSureAddress').click(function() {
		sureServiceAddress();//处理服务地址
	});
	//将当前登录用户的手机号码赋值给电话号标签
	/*var myPhone = I('myUserName');
	if (typeof myPhone != 'undefined' && phoneNumberIsRight(myPhone.value)) {
		I('soPhone').value = myPhone.value;
	}*/
	//发布服务
	$('#btnIssueService').click(function() {
		issueService();
	});
});

//查询所有服务类型
function queryAllServiceType() {
	$.ajax({
		type: 'post',
		url: SCS_INDEX_URL + 'userIndex/queryServiceType',
		dataType: 'json',
		async: false,
		timeout: 6000,
		//data: {cityId: currentCityId, cityName: currentCityName},
		success: function(result) {
			if (result.status == '0') {
				sessionStorage.setItem(SERVICE_TYPE_VALUE_LIST, result.serviceTypeList);
				//添加服务类型列表
				var serviceTypeStr = '';
				if (typeof serviceTypeArray == 'undefined') {
					serviceTypeArray = result.serviceTypeList;
				}
				for (var j = 0; j < serviceTypeArray.length; j++) {
					serviceTypeStr += '<option value="' + serviceTypeArray[j].P_BM + '">' + serviceTypeArray[j].NAME + '</option>';
				}
				$('#serviceType').append(serviceTypeStr);
			}
		}
	});
}
//显示遮罩层和地图
function showAMap() {
	//加载地图，调用浏览器定位服务
	map = new AMap.Map('aMapContainer', {
	    resizeEnable: true,
	    zoom:11
	});
	$('.mask-layer').show();
	$('#amapLayerCenter').show();
	//显示比例尺
	var aMapScale = new AMap.Scale({
	    visible: true
	});
	aMapScale.show();
	map.addControl(aMapScale);
	map.plugin('AMap.Geolocation', function() {
	    geolocation = new AMap.Geolocation({
	        enableHighAccuracy: true,//是否使用高精度定位，默认:true
	        timeout: 20000,          //超过10秒后停止定位，默认：无穷大
	        maximumAge: 3000,           //定位结果缓存0毫秒，默认：0
	        showCircle: false,        //定位成功后用圆圈表示定位精度范围，默认：true
	        buttonOffset: new AMap.Pixel(10, 20),//定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
	        zoomToAccuracy: true,      //定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
	        buttonPosition:'RB'
	    });
	    map.addControl(geolocation);
	    geolocation.getCurrentPosition();
	    AMap.event.addListener(geolocation, 'complete', onComplete);//返回定位信息
	    AMap.event.addListener(geolocation, 'error', onError);      //返回定位出错信息
	});
	//为地图注册click事件获取鼠标点击出的经纬度坐标
	map.on('click', function(e) {
	    clickLng = e.lnglat.getLng();
	    clickLat = e.lnglat.getLat();
	    //I('test').innerHTML = clickLng + ',' + clickLat;
	    if (mapMarker != null) {
	    	map.remove(mapMarker);//清除原来的标记
		}
	    
	    //给点击的地方添加标记
	    addMapMarker(clickLng, clickLat);
	    //给点标记添加移动结束时监听事件
	    AMap.event.addListener(mapMarker, 'dragend', mapMarkerDrag);
	    regeocoder();
	});
	
	//输入提示
	var autoOptions = {input: "tipinput"};
	var auto = new AMap.Autocomplete(autoOptions);
	placeSearch = new AMap.PlaceSearch({map: map});  //构造地点查询类
	AMap.event.addListener(auto, "select", select);//注册监听，当选中某条记录时会触发
	
}
//关闭遮罩层和地图
function closeMaskLayerAndMap() {
	$('.mask-layer').hide();
	$('#amapLayerCenter').hide();
}

//解析定位结果
function onComplete(data) {
	clickLng = data.position.getLng();
    clickLat = data.position.getLat();
    addMapMarker(clickLng, clickLat);
    map.setZoomAndCenter(14, [clickLng, clickLat]);
}
//如果解析精确定位错误，则根据定位的城市名来定位
function onError(data) {
	//map.setCity(currentCityName);
	showCityInfo();
}
//根据IP定位当前城市
function showCityInfo() {
    //实例化城市查询类
    var citysearch = new AMap.CitySearch();
    //自动获取用户IP，返回当前城市
    citysearch.getLocalCity(function(status, result) {
        if (status === 'complete' && result.info === 'OK') {
            if (result && result.city && result.bounds) {
                //var cityinfo = result.city;
                var citybounds = result.bounds;
                //地图显示当前城市
                map.setBounds(citybounds);
            }
        }
    });
}
//当选中某条记录时会触发
function select(e) {
	var eAddress = e.poi.district;
	var eCity = eAddress.split('市')[0] + '市';
	cityArea = eAddress.replace(eCity, '');
	if (isNull(currentCityName)) {
		currentCityName = localStorage.getItem(LAST_CITY_NAME_KEY);
		if (isNull(currentCityName)) {
			//重新定位城市名
			var citysearch = new AMap.CitySearch();
			//自动获取用户IP，返回当前城市
			citysearch.getLocalCity(function(status, result) {
			    if (status === 'complete' && result.info === 'OK') {
			        if (result && result.city && result.bounds) {
			        	currentCityName = result.city;
			        	getCityIdByName(currentCityName);
			        }
			    }
			});
		}
	}
	//判断当前浏览器定位的城市跟订单地址是否一致，不一致，则提示是否切换
	if (currentCityName != eCity) {
		if (confirm('您发布的服务的城市是<' + eCity + '>，是否切换到该城市？')) {
			currentCityName = eCity;
			getCityIdByName(currentCityName);
		}
	}
    placeSearch.setCity(e.poi.adcode);
    placeSearch.search(e.poi.name);  //关键字查询查询
    clickLng = e.poi.location.lng;
    clickLat = e.poi.location.lat;
	//I('test').innerHTML = clickLng + ',' + clickLat;
	//I('tipinput').value = e.poi.district + I('tipinput').value;
	addMapMarker(clickLng, clickLat);
	regeocoder(e.poi.name);
}
//点标记添加移动结束时监听事件函数
function mapMarkerDrag(e) {
	clickLng = e.lnglat.lng;
    clickLat = e.lnglat.lat;
	//I('test').innerHTML = clickLng + ',' + clickLat;
}
//通过经纬度添加地图标记
function addMapMarker(lng, lat) {
	mapMarker = new AMap.Marker({
   		map: map,
        icon: "http://webapi.amap.com/theme/v1.3/markers/n/mark_r.png",
        draggable: true,  //是否可拖动
        position: [lng, lat]
	});
}
//逆地理编码,经纬度转地址
function regeocoder(e_poi_name) {
	var lnglatXY = [clickLng, clickLat];
    var geocoder = new AMap.Geocoder({
        radius: 1000,
        extensions: "all"
    });
    geocoder.getAddress(lnglatXY, function(status, result) {
        if (status === 'complete' && result.info === 'OK') {
            geocoderCallBack(result,e_poi_name);
        }
    });
}
//逆地理编码成功后的回调函数
function geocoderCallBack(data,e_poi_name) {
    var address = data.regeocode.formattedAddress; //返回地址描述
    if (isNull(currentCityName)) {
    	currentCityName = data.regeocode.addressComponent.province;//设置当前默认城市名
	}
    if (isNull(cityArea)) {
    	cityArea = data.regeocode.addressComponent.district;//设置当前默认区县名
	}
    I("tipinput").value = address;
}
//处理服务订单地址
function sureServiceAddress() {
	var mapAddress = V("tipinput");
	if (isNull(clickLat) || isNull(clickLng) || isNull(mapAddress)) {
		alert('获取精确地址失败，请重新输入地址！');
		return;
	}
	closeMaskLayerAndMap();
	I('soAddress').value = mapAddress;
}
//发布服务
function issueService() {
	if (checkServiceForm()){
		var cityId = localStorage.getItem(LAST_CITY_ID_KEY);
		if (isNull(cityArea)){
			cityArea = currentCityName + '周边';
		}
		$('#btnIssueService').attr('disabled', true);
		$('#btnIssueService').attr('cursor', 'none');
		I('btnIssueService').innerHTML = '发布中...';
		
		$.ajax({
			type: 'post',
			url : SCS_INDEX_URL + 'serviceOrder/saveServiceOrder',
			dataType: 'json',
			async: true,
			timeout: 120000,
	        data: {
	        	'soName': V('soName'),'serviceDate': V('soServiceDate'),'soServiceType': V('serviceType'),'soCityid': cityId,'cityArea': cityArea,
	        	'soAddress': V('soAddress'),'soPhone': V('soPhone'),'soAmount': V('soAmount'),'soDesc': V('soDesc'),'soBillNumber':getBillNoPrefix(),
	        	'soPaymentMenthod': $('input[name="soPaymentMenthod"]:checked').val(), 'soLng': clickLng,'soLat': clickLat
	        },
	        success : function (result) {
	            if(result.success){
	            	alert('您的订单发布成功，快去看是谁在抢您的订单吧！');
	            	$('#serviceOrderForm')[0].reset();
	            }else{
	            	I("errorMsg").innerHTML = "当前系统繁忙，请稍候再试";
	            }
	            $('#btnIssueService').attr('disabled', false);
	    		$('#btnIssueService').attr('cursor', 'pointer');
	    		I('btnIssueService').innerHTML = '发布服务';
	        },
	        error : function (data) {
	        	I("errorMsg").innerHTML = "下单失败，请稍候再试";
	        	$('#btnIssueService').attr('disabled', false);
	    		$('#btnIssueService').attr('cursor', 'pointer');
	    		I('btnIssueService').innerHTML = '发布服务';
	        }
	    });
	}
}
/**
 * 检查表单必填项
 */
function checkServiceForm() {
	if (isNull(clickLat) || isNull(clickLng) || isNull(V("tipinput"))) {
		I('errorMsg').innerHTML = '获取精确地址失败，请重新输入地址！';
		return false;
	}
	if (isNull(I('soName').value)) {
		I('errorMsg').innerHTML = '请输入标题';
		return false;
	}
	if (isNull(I('soServiceDate').value)) {
		I('errorMsg').innerHTML = '请输入服务时间';
		return false;
	}
	if (isNull(I('soAddress').value)) {
		I('errorMsg').innerHTML = '请输入服务地址';
		return false;
	}
	if (isNull(I('soPhone').value) || !phoneNumberIsRight(I('soPhone').value)) {
		I('errorMsg').innerHTML = '请输入正确的手机电话';
		return false;
	}
	if (isNull(I('soAmount').value) || parseFloat(I('soAmount').value) < 0) {
		I('errorMsg').innerHTML = '请输入正确的金额，且不能小于0';
		return false;
	}
	return true;
}
/**
 * 获取单号前缀
 * @returns
 */
function getBillNoPrefix() {
	var clientNo = Math.floor(Math.random()*10) % 2;//客户编码
	var nowDate = new Date();
	var month = ('0' + (nowDate.getMonth() + 1)).slice(-2);
    var day = ('0' + (nowDate.getDate())).slice(-2);
    var fourRandom = '';//4位随机码
    for (var g = 0; g < 4; g ++){
    	fourRandom += Math.floor(Math.random() * 10);
    }
    return clientNo + month + day + fourRandom;
}