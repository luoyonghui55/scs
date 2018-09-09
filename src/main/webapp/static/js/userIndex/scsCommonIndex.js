/**
 * 通用主页JS
 */
//抢单前的判断  #e99494
var soId = '';//重新登录次数
function robServiceOrder(soBillNumber, btnGrabOrderNumber){
	if($('#btnGrabOrder' + btnGrabOrderNumber)[0].innerHTML != '我要抢单')
		return;
	if (isNull(soBillNumber)) {
		alert('抢单失败，请刷新后再试');
		return;
	}
	//不能抢自己发布的订单
	var scsUserId = sessionStorage.getItem('SCS_USER_ID');
	if(scsUserId == $('#issuerUserId_' + soBillNumber).val()){
		alert('您不能抢自己发布的订单');
		return;
	}
	var userName = sessionStorage.getItem(SCS_USER_NAME);
	soId = soBillNumber;
	sessionStorage.setItem('btnGrabOrder',btnGrabOrderNumber);//按钮ID编号
	sessionStorage.setItem('robingServiceOrderId',soId);
	if (isNull(userName)) {
		//alert('您还没有登录，请您登录后再抢单');
		initDrag();//初始化验证框
		$('.page-mask').show();
		$('#middleMain').show();
		//$('#drag').drag();
		drage = false;
		I('errorPrompt').innerHTML = '';
	}else {
		robingServiceOrder();
	}
}
//抢单
function robingServiceOrder() {
	var userName = sessionStorage.getItem(SCS_USER_NAME);
	if (isNull(userName)) {
		alert('您还没有登录，请重新登录');
		return;
	}
	var soId = sessionStorage.getItem('robingServiceOrderId');
	if (isNull(userName)) {
		alert('获取服务订单编号失败');
		return;
	}
	if(!confirm('您确定要抢该单服务吗?')){
		return;
	}
	$('#middleMain').hide();
	$('.page-mask').hide();
	var btnGrabOrderNumber = sessionStorage.getItem('btnGrabOrder');
	var btnGrabOrder = $('#btnGrabOrder' + btnGrabOrderNumber)[0];
	btnGrabOrder.innerHTML = '抢单中...';
	$.ajax({
		type : "POST",
		url : SCS_INDEX_URL + 'serviceOrder/robServiceOrder',
		data : {
			userName : userName,
			soBillNumber : soId
		},
		dataType : 'json',
		cache : false,
		async: false,
		success : function(result) {
			var msg = '', s = result.errorFlag;
			if ("0" == s && result.success) {
				msg = '抢单成功';
				$('#serviceInfo' + btnGrabOrderNumber).remove();
			} else {
				msg = '抢单失败';
			}
			$('.loading').hide();
			$('.page-mask').hide();
			sessionStorage.removeItem('btnGrabOrder');
			sessionStorage.removeItem('robingServiceOrderId');
			alert(msg);
		},
		error: function(errorResult) {
			$('.loading').hide();
			$('.page-mask').hide();
			sessionStorage.removeItem('btnGrabOrder');
			sessionStorage.removeItem('robingServiceOrderId');
			showLglTipsBox(300,70,'错误提示','抢单失败',false);
		}
	});
}