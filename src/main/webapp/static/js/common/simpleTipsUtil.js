//提示框工具类
/**
* 显示提示对话框
* @param width  对话框宽度
* @param height  对话框高度
* @param title   对话框标题
* @param content  对话框内容
* @param btnCloseIsShow  对话框中的取消按钮是否显示，true:显示，false:不显示
* 使用示例：showLglTipsBox(300,100,'测试','内容',true);
* 若点击“确定”后还需要处理其它事情，则需要重新添加确定按钮的点击事件，如：
* $(document).ready(function () {
      $('#btnLglTipsBoxOk').click(function () {
           alert('ok');
       });
   });
*/
function showLglTipsBox(width,height,title,content,btnCloseIsShow){
	$('.lgl-pop-background').remove();
	$('.lgl-pop').remove();
	var html = '<div class="lgl-pop-background" style="position: absolute;z-index: 129;left: 0;top: 0;width: 100%;height: 100%;background: rgba(0,0,0,.2);"></div>'+
            '<div class="lgl-pop" style="width:'+width+'px;height:'+height+'px;min-height: 100px;position: absolute;top: 0;left: 0;bottom: 0;right: 0; margin: auto;padding: 25px;z-index: 130;border-radius: 8px;background-color: #fff;box-shadow: 0 3px 18px rgba(100, 0, 0, .5);">'+
            '<div style="height:40px;width:100%;border-bottom: 1px #E5E5E5 solid;margin-top:-40px;"><h4>'+title+'</h4></div><div>'+content+'</div>'+
            '<div style="height:50px;line-height:50px;width:100%;border-top: 1px #E5E5E5 solid;text-align: right; bottom: 20px; margin-top: '+(height-50)+'px;">'+
            (btnCloseIsShow ? '<input type="button" value="关闭" onclick="closeLglTipsBox()" style="padding:8px 15px;margin:15px 5px;border-radius: 5px;cursor:pointer;border:0;background: url(static/images/userIndex/menu-spacer.gif) repeat-x;color: #fff;">' : '')+
            '<input id="btnLglTipsBoxOk" type="button" value="确定" onclick="closeLglTipsBox()" style="padding:8px 15px;margin:15px 5px;border: 0;border-radius: 5px;background: url(static/images/userIndex/menu-spacer.gif) repeat-x;color: #fff;cursor:pointer;"></div></div>';
    $(document.body).append(html);
}
/**
 * 隐藏提示框口
 * @returns
 */
function closeLglTipsBox(){
	$('.lgl-pop-background,.lgl-pop').hide();
}
/**
 * 显示之前关闭的提示框口
 * @returns
 */
function showClosedLglTipsBox(){
	$('.lgl-pop-background,.lgl-pop').show();
}
//全局Ajax请求，当请求失败时，会执行些函数
$(document).ajaxError(function(event, request, settings) {
	var sessionStatus = request.getResponseHeader("session-status");
	if(sessionStatus == 'timeout'){
		sessionStorage.removeItem(SCS_USER_NAME);
		closeLglTipsBox();
		sessionStorage.setItem('lastRequestUrl',window.location.href);//本次要请求的URL
		alert('登录失效，请重新登录');
		window.location.href = SCS_INDEX_URL + '/userPages/userLogin.html';
	}
});