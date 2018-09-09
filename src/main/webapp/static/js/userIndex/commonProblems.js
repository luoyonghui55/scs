/**
 * 常见问题
 */
$(function(){
    $('.question').click(function(){
        var nextLi = $(this).next();//获取下一个兄弟元素
        //如果nextLi节点隐藏了，则显示，否则就隐藏
        if (nextLi.is(':hidden')){
            nextLi.show();
        }else {
            nextLi.hide();
        }
    });
});