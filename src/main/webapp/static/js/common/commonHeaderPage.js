var scsUserName = sessionStorage.getItem(SCS_USER_NAME);
if (scsUserName != null && scsUserName != '') {
	var Myself = I('Myself');
	$('.user-register').hide();
	$('.user-login').hide();
	$('#IamLogout').show();
	var a = document.createElement('a');
	a.id = 'myUserName';
	a.href = SCS_INDEX_URL + 'userPages/myServiceOrder.html';
	a.innerText = scsUserName;
	Myself.appendChild(a);
}else {
	$('#myUserName').remove();
	$('.user-register').show();
	$('.user-login').show();
}

$(function() {
	$('#IamLogout').click(function() {
		userLogout();
	});
});
//退出
function userLogout() {
	var scsUserName = sessionStorage.getItem(SCS_USER_NAME);
	if (scsUserName != null && scsUserName != '') {
		$.ajax({
			type : "POST",
			url : SCS_INDEX_URL + '/userIndex/user/toLogout',
			dataType : 'json',
			cache : false,
			async: false,
			success : function(result) {
				var msg = '', s = result.status;
				if ("0" == s) {
					sessionStorage.removeItem(SCS_USER_NAME);
					$('#myUserName').remove();
					$('.user-register').show();
					$('.user-login').show();
					$('#IamLogout').hide();
				} else {
					alert('退出失败');
				}
			},
			error: function() {
				alert('退出失败');
			}
		});
	}else {
		$('#myUserName').remove();
		$('.user-register').show();
		$('.user-login').show();
		$('#IamLogout').hide();
	}
}