/**
 * 用户登录
 */
var drage = false, scsToken = '';
$(function() {
	$('#btnUserLogin').click(function() {
		var userName = V('userName');
		var userPassword = V('userPassword');
		if (isNull(userName) || isNull(userPassword)) {
			I('errorPrompt').innerHTML = '请输入用户和密码';
			return;
		}
		if (!drage || isNull(scsToken)) {
			I('errorPrompt').innerHTML = '请拖动滑块验证';
			return;
		}
		if (!isNull(sessionStorage.getItem('btnGrabOrder'))) {
			$('#middleMain').css('z-index', 8000);
			$('.loading').show();
			$('#loadingWord').text('正在登录...');
		}
		I('errorPrompt').innerHTML = '正在登录...';
		iAmLogin(userName,userPassword);
	});
});
//我要登录
function iAmLogin(userName,userPassword) {
	$('#btnUserLogin').attr("disabled",true);
	var shaObj = new jsSHA("SHA-1", "TEXT");
	shaObj.update(userPassword);
	var hashPassword = shaObj.getHash("HEX");
	$.ajax({
		type : "POST",
		url : SCS_INDEX_URL + '/userIndex/user/toLogin',
		data : {
			userName : userName,
			userPassword : hashPassword,
			scsToken : scsToken
		},
		dataType : 'json',
		cache : false,
		async: false,
		success : function(result) {
			var msg = '', s = result.status;
			I('errorPrompt').innerHTML = '';
			if ("0" == s) {
				//SCS_USER_NAME = result.loginSuccessFlag;
				sessionStorage.setItem(SCS_USER_NAME, userName);
				sessionStorage.setItem(SCS_USER_PHONE, result.userPhone);
				sessionStorage.setItem(SCS_USER_ID, result.userId);
				var lastRequestUrl = sessionStorage.getItem('lastRequestUrl');
				//如果上一次请求路径不为空，则直接跳转到上一次请求的路径
				if(!isNull(lastRequestUrl)){
					sessionStorage.removeItem('lastRequestUrl');
					window.location.href = lastRequestUrl;
				}else{
					var thisPagePath = window.document.location.href;
					if (thisPagePath.indexOf('userLogin') > -1) {//如果是登录页面，就跳转到首页，否则不跳转
						window.location.href = SCS_INDEX_URL;
					}
				}
				
				if (!isNull(sessionStorage.getItem('btnGrabOrder'))) {
					//$('#middleMain').hide();
					$('#middleMain').css('z-index', 9006);
					//$('.page-mask').hide();
					//显示用户名，隐藏注册、登录按钮
					$('.user-register').hide();
					$('.user-login').hide();
					$('#IamLogout').show();
					var a = document.createElement('a');
					a.href = SCS_INDEX_URL + 'userPages/myServiceOrder.html';
					a.id = 'myUserName';
					a.innerText = userName;
					I('Myself').appendChild(a);
					$('#loadingWord').text('正在抢单...');
					robingServiceOrder();
				}
			} else if (s == '1') {
				msg = '用户名或密码不能为空';
			} else if (s == '2' || s == '4') {
				msg = '验证失败，请重新登录';
			} else if (s == '3') {
				msg = '用户名或密码错误';
			} else if (s == '5') {
				msg = '您的账户已被冻结，如有疑问请联系客户';
			}
			if ("0" != s) {
				$('.loading').hide();
				$('#middleMain').css('z-index', 9006);
				sessionStorage.removeItem(SCS_USER_NAME);
			}
			$('#btnUserLogin').attr("disabled",false);
			I('errorPrompt').innerHTML = msg;
			initDrag();
		},
		error: function() {
			$('.loading').hide();
			initDrag();
			$('#middleMain').css('z-index', 9006);
			$('#btnUserLogin').attr("disabled",false);
			I('errorPrompt').innerHTML = '登录失败';
			sessionStorage.removeItem(SCS_USER_NAME);
		}
	});
}