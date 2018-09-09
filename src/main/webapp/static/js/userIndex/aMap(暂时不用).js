var map,geolocation, mapMarker,clickLng,clickLat,placeSearch,serviceCity;
//加载地图，调用浏览器定位服务
map = new AMap.Map('aMapContainer', {
    resizeEnable: true,
    zoom:11
});
//显示比例尺
var aMapScale = new AMap.Scale({
    visible: false
});
aMapScale.show();
map.addControl(aMapScale);
map.plugin('AMap.Geolocation', function() {
    geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,//是否使用高精度定位，默认:true
        timeout: 10000,          //超过10秒后停止定位，默认：无穷大
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
    I('test').innerHTML = clickLng + ',' + clickLat;
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


//解析定位结果
function onComplete(data) {
  clickLng = data.position.getLng();
  clickLat = data.position.getLat();
  addMapMarker(clickLng, clickLat);
}
//如果解析精确定位错误，则根据定位的城市名来定位
function onError(data) {
  map.setCity(currentCityName);
}
//当选中某条记录时会触发
function select(e) {
	var eAddress = e.poi.district;
	var eCity = eAddress.split('市')[0] + '市';
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
			            
			            /*if (last_city_name == currentCityName) {
			            	currentCityId = localStorage.getItem(LAST_CITY_ID_KEY);
						}else {
							currentCityId = '';
							localStorage.setItem(LAST_CITY_ID_KEY, '');
				            localStorage.setItem(LAST_CITY_NAME_KEY, currentCityName);
						}*/
			        }
			    }
			});
		}
	}
	//判断当前浏览器定位的城市跟订单地址是否一致，不一致，则提示是否切换
	if (currentCityName != eCity) {
		if (confirm('您发布的服务的城市是<' + eCity + '>，是否切换到该城市？')) {
			currentCityName = eCity;
			localStorage.setItem(LAST_CITY_ID_KEY, '');
          localStorage.setItem(LAST_CITY_NAME_KEY, currentCityName);
		}
	}
    placeSearch.setCity(e.poi.adcode);
    placeSearch.search(e.poi.name);  //关键字查询查询
    clickLng = e.poi.location.lng;
    clickLat = e.poi.location.lat;
	I('test').innerHTML = clickLng + ',' + clickLat;
	I('tipinput').value = e.poi.district + I('tipinput').value;
	addMapMarker(clickLng, clickLat);
	regeocoder(e.poi.name);
}
//点标记添加移动结束时监听事件函数
function mapMarkerDrag(e) {
	clickLng = e.lnglat.lng;
  clickLat = e.lnglat.lat;
	I('test').innerHTML = clickLng + ',' + clickLat;
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
  I("tipinput").value = address;
}