/**通用工具类*/
/*** 是否为空，空：true,不空：false*/
function isNull(str){if(str==null||str==''||typeof str=='undefined'){return true;}return false;};function I(id) {return document.getElementById(id);};function V(id) {return I(id).value;};
/*设置按钮可点击状态*/function setButtonStatus(btn, time){btn.disabled = true;setTimeout(function () {btn.disabled = false;}, time);}
//function getRootPath(){var b=location.href;var d=window.document.location.pathname;var c=b.indexOf(d);var a=b.substring(0,c);return a};