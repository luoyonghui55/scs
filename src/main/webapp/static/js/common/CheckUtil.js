/**
 * 检查input标签的值是否为空,空则返回false
 * @param id input标签的ID
 */
function inputIsEmpty(id) {
    var obj = I(id);
    if (typeof(obj) == 'undefined' || obj.value == null || obj.value == ""){
        return false;
    }
    return true;
}
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
/**
 * 判断是否存在寄件地址
 * 存在返回true,否则返回false
 */
function isHaveSendAddress() {
    var sendAddress = sessionStorage.getItem("mySendAddress_0");
    if (typeof sendAddress == 'undefined' || sendAddress == null || sendAddress == ''){
        return false;
    }
    return true;
}
/**
 * 判断是否存在收件地址
 * 存在返回true,否则返回false
 */
function isHaveReceiveAddress() {
    if (receiverNumber >= 1) {
    	for (var i = 1; i <= receiverNumber; i++) {
    		var rAddress = sessionStorage.getItem("myReceiveAddress_" + i);
    	    if (typeof rAddress == 'undefined' || rAddress == null || rAddress == ''){
    	        return false;
    	    }
    	}
	}
    return true;
}
/**
 * 获取当前日期时间字符串
 * @param isShowSeconds  0:显示秒钟，1：不显示秒钟
 * @constructor
 */
function GetCurrentDateTimeStr(isShowSeconds) {
    var now = new Date();
    var year = now.getFullYear() + "-";
    var month = ((now.getMonth() + 1) < 10 ? ('0' + (now.getMonth() + 1)) : (now.getMonth() + 1)) + "-";
    var day = (now.getDate() < 10 ? ('0' + now.getDate()) : now.getDate()) + " ";
    var hh = (now.getHours() < 10 ? ('0' + now.getHours()) : now.getHours()) + ":";
    var mm = (now.getMinutes() < 10 ? ('0' + now.getMinutes()) : now.getMinutes());
    var ss = (now.getSeconds() < 10 ? ('0' + now.getSeconds()) : now.getSeconds());
    var dateTimeStr;
    if(isShowSeconds == 0){
    	dateTimeStr = year + month + day + hh + mm + ":" + ss;
    }else{
    	dateTimeStr = year + month + day + hh + mm;
    }
    return dateTimeStr;
}
/**
 * 日期字符串转时间戳
 * @param dateStr
 * @constructor
 */
function DateStrToMilliseconds(dateStr) {
    var milliseconds = '';
    if (dateStr != null && dateStr != ""){
        dateStr = dateStr.replace(/-/g,'/');
        var now = new Date(dateStr);
        milliseconds = now.getTime();
    }
    return milliseconds;
}
/**
 * 判断用户是否登录
 * 登录返回ture,未登录返回false
 */
function userIsLogin() {
    var userId = sessionStorage.getItem('userId');
    if (userId != null && trim(userId) != ''){
        return true;
    }
    return false;
}

/**
 * 验证邮箱,成功返回true
 */
function checkEmail(val) {
	if(val != null && val.trim() != ""){
		var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
		if(!reg.test(val)){
			//$("#promptMsg").html("邮箱格式不正确");
			return false;
		}
		return true;
	}
	return false;
}

function getRootPath(){
	//var strFullPath = window.document.location.href;
	var strFullPath = location.href;
	var strPath = window.document.location.pathname;
	var pos = strFullPath.indexOf(strPath);
	var prePath = strFullPath.substring(0, pos);
	if (strFullPath.indexOf('365bangbang') <= 0) {
		var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
		prePath = prePath + postPath;
	}
	return prePath;
}
/**
 * 判断某城市是否开放服务
 * @param cityName  城市名称，0：已开放，1：未开放
 * @constructor
 */
/*function getCityIdByName(cityName) {
    var cityId = 0;
    var errorMsg = '';
    //如果cityName不为空
    if (cityName != null && trim(cityName) != ''){
        $.ajax({
            type: 'post',
            url : '/365CityWeb/mobile/mobile_AddDeal!cityIsOpenService.action',
            data: {cityName: cityName},
            dataType: 'json',
            success: function (result) {
                if (result[0].success){
                    //sessionStorage.setItem("cityId", result[0].cityId);
                    cityId = result[0].cityId;
                }else {
                    errorMsg = '暂未开通';
                }
            },
            error  : function (result) {
                errorMsg = '判断该城市是否开放服务时出错，请稍后再试';
            }
        });
    }else {
        errorMsg = '未获取到该城市名';
    }
    return cityId + '@#' + errorMsg;
}*/
/**
 * 删除左右两端的空格
 */
function trim(str){
    return str.replace(/(^\s*)|(\s*$)/g, "");
}
/**
 * 判断字符串是否包含中文，不包含返回true
 * @param str
 * @returns {Boolean}
 */
function isHaveChina(str){
	if(/.*[\u4e00-\u9fa5]+.*$/.test(str)){
		//alert("不能含有汉字！");
		return false;
	}
	return true;
}
/**
 * 解决输入框被手机弹起的键盘遮挡的问题
 * 在部分浏览器中，能自动将网页往上顶，解决这个遮挡问题，
 * 本次测试的浏览器有：google，QQ、华为荣耀6安卓4.4系统自带浏览器，
 * 安卓4.4系统自带浏览器中存在键盘遮挡问题
 */
/*var timer, windowInnerHeight;
var allInput = document.getElementsByTagName("input");
for (var i = 0; i < allInput.length; i ++){
    var inputObject = allInput[i];
    if (inputObject.type == 'text'){
        //给输入文本框添加点击和焦点监听事件
        inputObject.addEventListener('click', changePageHeightByKeyboard, false);
        inputObject.addEventListener('blur', changePageHeightByKeyboard, false);
    }
}
var j = 0;
document.getElementById('test').innerHTML = window.innerHeight + ",screen=" + screen.availHeight + ",clientHeight=" + document.body.clientHeight;
//clientHeight=649
function changePageHeightByKeyboard(e){
	if(j == 0){
		document.getElementById('test').innerHTML = window.innerHeight + ",screen=" + screen.availHeight + ",clientHeight=" + document.body.clientHeight;
		//clientHeight=668
	}
	j ++;
    if (e) { //blur,focus事件触发的
        document.getElementById('dv').innerHTML = window.innerHeight + ',android键盘' + (e.type == 'click' ? '弹出' : '隐藏') + '--通过' + e.type + '事件';
        //$('#dv').html('android键盘' + (e.type == 'focus' ? '弹出' : '隐藏') + '--通过' + e.type + '事件');
        if (e.type == 'click') {//如果是点击事件启动计时器监控是否点击了键盘上的隐藏键盘按钮，没有点击这个按钮的事件可用，keydown中也获取不到keyCode值
            setTimeout(function () {//由于键盘弹出是有动画效果的，要获取完全弹出的窗口高度，使用了计时器
                windowInnerHeight = window.innerHeight;//获取弹出android软键盘后的窗口高度
                
                document.getElementById('dv').innerHTML = window.innerHeight + ",screen=" + screen.availHeight + "," + document.body.clientHeight;
                if (window.innerHeight > windowInnerHeight) {
                    //alert('window.innerHeight=' + window.innerHeight + ',windowInnerHeight=' + windowInnerHeight);
                    
                    document.getElementById('dv').innerHTML = 'android键盘隐藏++++通过点击键盘隐藏按钮,window.innerHeight=' + 
                    window.innerHeight + ',windowInnerHeight=' + windowInnerHeight + ",clientHeight=" + document.body.clientHeight;
                    clearInterval(timer);
                }
                
                
                timer = setInterval(function () { changePageHeightByKeyboard(); }, 2000);
            }, 5000);
        }else {
        	//setTimeout(function(){
        		document.getElementById('dv').innerHTML = window.innerHeight + ','+ screen.availHeight + ",clientHeight=" + document.body.clientHeight + 
        		',android键盘' + (e.type == 'click' ? '弹出' : '隐藏') + '>>>>通过' + e.type + '事件';
                clearInterval(timer);
                //clientHeight=687
        	//}, 2000);
            
        }
    }else { //计时器执行的，需要判断窗口可视高度，如果改变说明android键盘隐藏了
    	document.getElementById('t1').innerHTML = j;
    	//document.getElementById('dv').innerHTML = 'android键盘,window=' + window.innerHeight + ",screen=" + screen.availHeight + ",clientHeight=" + document.body.clientHeight;
    	document.getElementById('dv').innerHTML = 'android键盘,i=' + window.innerHeight + ",s=" + screen.availHeight + ",c=" + document.body.clientHeight +
    	',sc=' + document.body.scrollHeight + ',o='+document.body.offsetHeight;
    	
    	if (window.innerHeight > windowInnerHeight) {
            //alert('window.innerHeight=' + window.innerHeight + ',windowInnerHeight=' + windowInnerHeight);
            
            document.getElementById('dv').innerHTML = 'android键盘隐藏--通过点击键盘隐藏按钮,window.innerHeight=' + window.innerHeight + 
            ',windowInnerHeight=' + windowInnerHeight + ",clientHeight=" + document.body.clientHeight;
            clearInterval(timer);
        }
    }
}*/