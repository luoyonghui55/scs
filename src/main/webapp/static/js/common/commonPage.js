//----- 用于控制页面footer定位到页面底部------start
var pageFooter = document.getElementById('footer');
var scrollHeight = document.body.scrollHeight;
var bodyClientHeight = document.body.clientHeight;
//控制页面尾部的位置
if (scrollHeight <= bodyClientHeight){
    pageFooter.style.position = 'fixed';
    pageFooter.style.bottom = 0;
}
//----- 用于控制页面footer定位到页面底部------end

//-----用于控制middle-main的高度----------start
//头部DIV高度
var pageHeaderHeight = document.getElementById('pageHeader').clientHeight;
var footerHeight = pageFooter.clientHeight;
var middleMain = document.getElementById('middleMain');
//中间DIV高度
var middleHeight = middleMain.clientHeight;
if ((pageHeaderHeight + footerHeight + middleHeight) < bodyClientHeight){
    middleMain.style.height = (bodyClientHeight - (pageHeaderHeight + footerHeight)) + 'px';
}