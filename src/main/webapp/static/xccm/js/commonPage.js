//----- 用于控制页面footer定位到页面底部------start
function setPageHeight() {
	var pageFooter = document.getElementById('footer');
	var scrollHeight = document.body.scrollHeight;
	//var scrollHeight = document.getElementById("bodyDiv").scrollHeight;
	//var availHeight = window.screen.availHeight;
	var bodyClientHeight = document.body.clientHeight;
	//alert("a=" + scrollHeight + ",b=" + availHeight + ',cc=' + cc);
	//控制页面尾部的位置
	if (scrollHeight <= bodyClientHeight){
	    pageFooter.style.position = 'fixed';
	    pageFooter.style.bottom = 0;
	}
	//----- 用于控制页面footer定位到页面底部------end

	//-----用于控制middle-main的高度----------start
	//头部DIV高度
	var pageHeaderHeight = document.getElementById('pageHeader').clientHeight + 100;
	var footerHeight = pageFooter.clientHeight;
	var middleMain = document.getElementById('middleMain');
	//中间DIV高度
	var middleHeight = middleMain.clientHeight;
	if ((pageHeaderHeight + footerHeight + middleHeight) < bodyClientHeight){
	    middleMain.style.height = (bodyClientHeight - (pageHeaderHeight + footerHeight)) + 'px';
	}
	//-------用于控制middle-main的高度----------end
}