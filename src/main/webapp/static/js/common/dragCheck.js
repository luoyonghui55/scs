/*
 * 拖动滑块
 */
var scsDragCheckHandler, scsDragCheckDrag,scsDragCheckText,scsDragCheckMaxWidth;
(function($){
    $.fn.drag = function(options){
        var x, isMove = false, defaults = {};
        scsDragCheckDrag = this;
        var options = $.extend(defaults, options);
        //添加背景，文字，滑块
        var html = '<div class="drag_bg"></div>'+
                    '<div class="drag_text" onselectstart="return false;" unselectable="on">向右拖动滑块验证</div>'+
                    '<div class="handler handler_bg"></div>';
        this.append(html);
        
        //var handler = drag.find('.handler');
        scsDragCheckHandler = scsDragCheckDrag.find('.handler');
        var drag_bg = scsDragCheckDrag.find('.drag_bg');
        //var text = drag.find('.drag_text');
        scsDragCheckText = scsDragCheckDrag.find('.drag_text');
        //var maxWidth = drag.width() - handler.width();  //能滑动的最大间距
        scsDragCheckMaxWidth = scsDragCheckDrag.width() - scsDragCheckHandler.width();  //能滑动的最大间距
        
        //鼠标按下时候的x轴的位置
        scsDragCheckHandler.mousedown(function(e){
            isMove = true;
            x = e.pageX - parseInt(scsDragCheckHandler.css('left'), 10);
        });
        
        //鼠标指针在上下文移动时，移动距离大于0小于最大间距，滑块x轴位置等于鼠标移动距离
        $(document).mousemove(function(e){
            var _x = e.pageX - x;
            if(isMove){
                if(_x > 0 && _x <= scsDragCheckMaxWidth){
                    //handler.css({'left': _x});
                	scsDragCheckHandler.css({'left': _x});
                    drag_bg.css({'width': _x});
                }else if(_x > scsDragCheckMaxWidth){  //鼠标指针移动距离达到最大时清空事件
                    dragOk();
                }
            }
        }).mouseup(function(e){
            isMove = false;
            var _x = e.pageX - x;
            if(_x < scsDragCheckMaxWidth){ //鼠标松开时，如果没有达到最大距离位置，滑块就返回初始位置
            	scsDragCheckHandler.css({'left': 0});
                drag_bg.css({'width': 0});
            }
        });
    };
})(jQuery);
//清空事件
function dragOk(){
	drage = true;
	scsToken = 'C0525B8802AC5790EEA4593684A70F';
	scsDragCheckHandler.removeClass('handler_bg').addClass('handler_ok_bg');
	scsDragCheckText.text('验证通过');
    scsDragCheckDrag.css({'color': '#fff'});
    scsDragCheckHandler.unbind('mousedown');
    $(document).unbind('mousemove');
    $(document).unbind('mouseup');
    $("#errorPrompt").text('');
}
//初始化验证框
function initDrag(){
    drage = false;
    isMove = false;
    var drag_bg = $('.drag_bg');
    scsDragCheckHandler.css({'left': 0});//滑块就返回初始位置
    drag_bg.css({'width': 0});

    scsDragCheckHandler.removeClass('handler_ok_bg').addClass('handler_bg');
    scsDragCheckText.text('向右拖动滑块验证');
    $('.drag_text').css('color','black');
    var x;
    //鼠标按下时候的x轴的位置
    scsDragCheckHandler.mousedown(function(e){
        isMove = true;
        x = e.pageX - parseInt(scsDragCheckHandler.css('left'), 10);
    });

    //鼠标指针在上下文移动时，移动距离大于0小于最大间距，滑块x轴位置等于鼠标移动距离
    $(document).mousemove(function(e){
        var _x = e.pageX - x;
        if(isMove){
            if(_x > 0 && _x <= scsDragCheckMaxWidth){
            	scsDragCheckHandler.css({'left': _x});
                drag_bg.css({'width': _x});
            }else if(_x > scsDragCheckMaxWidth){  //鼠标指针移动距离达到最大时清空事件
                dragOk();
            }
        }
    }).mouseup(function(e){
        isMove = false;
        var _x = e.pageX - x;
        if(_x < scsDragCheckMaxWidth){ //鼠标松开时，如果没有达到最大距离位置，滑块就返回初始位置
        	scsDragCheckHandler.css({'left': 0});
            drag_bg.css({'width': 0});
        }
    });
}