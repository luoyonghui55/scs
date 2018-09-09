/**
 * 我的服务订单
 */
//soType,0:发布订单，1:已抢订单
var nowDate = new Date(), myServiceOrderCurrentPage = 1, pageLimit = 10, soType = 0, queryServiceOrderStatus = '';
var nowYear = nowDate.getFullYear() + "-";
var nowMonth = ((nowDate.getMonth() + 1) < 10 ? ('0' + (nowDate.getMonth() + 1)) : (nowDate.getMonth() + 1));
var nowYearMonth = nowYear + nowMonth;
$(function(){
    $('.service-status span').click(function(){//#serviceStatus
        $('.service-status span').removeClass();
        $(this).addClass('service-status-selected');
        queryServiceOrderStatus = getStatus(this.id);
        myServiceOrderCurrentPage = 1;
        queryMyServiceOrderData(myServiceOrderCurrentPage, pageLimit, queryServiceOrderStatus);
    });
    $('.search-button').click(function() {
    	myServiceOrderCurrentPage = 1;
    	//var selectedStatusId = '';
    	if (this.id == 'btnDealDetailSearch') {
    		myServiceOrderCurrentPage = 1;
    		queryMyDealLog(myServiceOrderCurrentPage,pageLimit,getDealType(''));
		}else {
			if (this.id == 'btnIssueServiceSearch') 
				soType = 0;
	    	else 
	    		soType = 1;
	    	queryMyServiceOrderData(myServiceOrderCurrentPage, pageLimit, queryServiceOrderStatus);
		}
	});
    $('#dealType span').click(function(){
        $('#dealType span').removeClass();
        $(this).addClass('deal-type-selected');
        myServiceOrderCurrentPage = 1;
        queryMyDealLog(myServiceOrderCurrentPage,pageLimit,getDealType(this.id));
    });
    $('#leftMenu ul li').click(function(){
        if (this.className != 'my-selected'){
            //修改标签样式
            $('#leftMenu ul li').removeClass();
            $(this).addClass('my-selected');
            var showId,hideId;
            if (this.id == 'myIssueOrder'){
                //显示我的订单对应的部分
                showId = ['issueServiceOrderSearch','issueServiceOrderDetailDiv','issueServicePageToolbar'];
                hideId = ['myDealSearch','myDealDetail','myMoney','dealDetailPageToolbar',
                              'robServiceOrderSearch','robServiceOrderDetailDiv','robServicePageToolbar'];
                myServiceOrderCurrentPage = 1;
                hideObject(hideId);
                showObject(showId);
                if ($('#issueServiceOrderDetail tbody').find('tr').length == 0) {//如果还没有加载
                	myServiceOrderCurrentPage = 1;
                	queryServiceOrderStatus = '';
                    queryMyServiceOrderData(myServiceOrderCurrentPage, pageLimit, queryServiceOrderStatus);
				}else {
					$('#issueServicePage a').each(function(index) {
						if (this.className.indexOf('current') > 0) {
							myServiceOrderCurrentPage = parseInt(this.innerText);
							return false;
						}
					});
				}
            }else if(this.id == 'myRobOrder'){
            	hideId = ['issueServiceOrderSearch','issueServiceOrderDetailDiv','issueServicePageToolbar',
            	          'myDealSearch','myDealDetail','myMoney','dealDetailPageToolbar'];
            	showId = ['robServiceOrderSearch','robServiceOrderDetailDiv','robServicePageToolbar'];
            	soType = 1;
            	myServiceOrderCurrentPage = 1;
            	queryServiceOrderStatus = '';
            	//$("#robServiceOrderDetail tbody").empty();
                queryMyServiceOrderData(myServiceOrderCurrentPage, pageLimit, queryServiceOrderStatus);
            }else {
                //隐藏我的订单对应的部分
                hideId = ['issueServiceOrderSearch','issueServiceOrderDetailDiv','issueServicePageToolbar',
                              'robServiceOrderSearch','robServiceOrderDetailDiv','robServicePageToolbar'];
                showId = ['myDealSearch','myDealDetail','myMoney','dealDetailPageToolbar'];
                myServiceOrderCurrentPage = 1;
                hideObject(hideId);
                showObject(showId);
                queryMyDealLog(myServiceOrderCurrentPage,pageLimit,getDealType(''));
            }
            hideObject(hideId);
            showObject(showId);
        }
    });
    setQueryInputDate();//设置查询日期时间
    $('#myIssueOrder').click(function() {
    	myServiceOrderCurrentPage = 1;
        queryMyServiceOrderData(myServiceOrderCurrentPage, pageLimit, '0');
	});
    queryMyMakeMoney();
    //初始化已发布订单数据
    myServiceOrderCurrentPage = 1;
    queryMyServiceOrderData(myServiceOrderCurrentPage, pageLimit, queryServiceOrderStatus);
    var soDetailScrollRight = 238;
    //将发布订单右边的操作列固定在右边
    /*$("#issueServiceOrderDetailDiv").scroll(function() {
        var isoDetailScrollLeft = $("#issueServiceOrderDetailDiv").scrollLeft();//获取滚动的距离
        var isoDetailTable = $("#issueServiceOrderDetailDiv table").eq(0).find("tr");//获取表格的所有tr
        var rightDistance = soDetailScrollRight < isoDetailScrollLeft ? 0 : (soDetailScrollRight - isoDetailScrollLeft);
        isoDetailTable.each(function(i){//对每一个tr（每一行）进行处理
            $(this).children().eq(6).css({"right": rightDistance});
        });
    });
	//将已抢订单右边的操作列固定在右边
    $("#robServiceOrderDetailDiv").scroll(function() {
        var rsoDetailScrollLeft = $("#robServiceOrderDetailDiv").scrollLeft();//获取滚动的距离
        var rsoDetailTable = $("#robServiceOrderDetailDiv table").eq(0).find("tr");//获取表格的所有tr
        var rightDistance = soDetailScrollRight < rsoDetailScrollLeft ? 0 : (soDetailScrollRight - rsoDetailScrollLeft);
        rsoDetailTable.each(function(i){//对每一个tr（每一行）进行处理
            $(this).children().eq(6).css({"right": rightDistance});
        });
    });*/
});
//设置查询日期时间
function setQueryInputDate(){
	$("#myDealLogDate").jeDate({
        format: "YYYY-MM",
        maxDate: $.nowDate(0)
    });
	$("#myIssueServiceOrderDate").jeDate({
        format: "YYYY-MM",
        maxDate: $.nowDate(0)
    });
    $("#myRobServiceOrderDate").jeDate({
        format: "YYYY-MM",
        maxDate: $.nowDate(0)
    });
    $("#myIssueServiceOrderDate").val(nowYearMonth);
    $("#myRobServiceOrderDate").val(nowYearMonth);
    $("#myDealLogDate").val(nowYearMonth);
}
//隐藏
function hideObject(idArray) {
	if(!isNull(idArray) && idArray.length > 0){
		for (var i = 0; i < idArray.length; i++) {
			$('#' + idArray[i]).hide();
		}
	}
}
//显示
function showObject(idArray) {
	if(!isNull(idArray) && idArray.length > 0){
		for (var i = 0; i < idArray.length; i++) {
			$('#' + idArray[i]).show();
		}
	}
}

function queryMyServiceOrderData(currentPage, pageSize,soStatus) {
	var soName = '', soOrderDate = '', soServiceDate = '';
	if (soType == 0) {
		soName = $('#myIssueServiceTitle').val();
		soOrderDate = $('#myIssueServiceOrderDate').val();
		if (isNull(soOrderDate)) {
			alert('日期不能为空');
			return;
		}
	}else if (soType == 1) {
		soName = $('#myRobServiceTitle').val();
		soServiceDate = $('#myRobServiceOrderDate').val();
		if (isNull(soServiceDate)) {
			alert('日期不能为空');
			return;
		}
	}
	
	if(soName == '输入标题...')
		soName = '';
	$.ajax({
		type: 'post',
		url: SCS_INDEX_URL + 'serviceOrder/queryMyServiceOrder',
		dataType: 'json',
		async: false,
		timeout: 30000,
		global: false,//不触发全局 AJAX 事件
		data: {
			soStatus: soStatus,
			soType: soType,
			soOrderDate: soOrderDate,
			soServiceDate: soServiceDate,
			currentPage: currentPage,
			showCount: pageSize,
			so_name: soName
		},
		success: function(result) {
			if (result.status == '0') {
				//服务订单数据
				var appendStr = '';
				var serviceOrderArray = result.serviceOrderList, totalRows = 0, totalAmount = 0;
				for (var s = 0; s < serviceOrderArray.length; s++) {
					var order = serviceOrderArray[s];
					totalRows ++;
					totalAmount += parseFloat(order.so_amount);
					var showBillNo = order.so_bill_number;
					showBillNo = showBillNo.substring(0,4) + ' ' + showBillNo.substring(4,8) + ' ' + showBillNo.substring(8,12);
					var otherFullPhone = soType == 0 ? order.rob_phone : order.issue_phone;
					var notFullPhone = '';
					if (!isNull(otherFullPhone)) {
						notFullPhone = otherFullPhone.substr(0,3) + '****' + otherFullPhone.substr(7,11);
					}
					
					appendStr += '<tr height="40px">'+
				                    '<td><a style="text-decoration:underline;" href="' + SCS_INDEX_URL + 'userIndex/showOrderDetail?oid=' + order.so_bill_number + '">'+showBillNo+'</a></td>'+
				                    '<td>'+order.so_name+'</td>'+
				                    '<td>'+order.so_order_date+'</td>'+
				                    '<td title="'+otherFullPhone+'">'+notFullPhone+'</td>'+
				                    '<td>'+order.so_amount+'</td>'+
				                    '<td>'+order.so_address+'</td>';
					var soStatus = '', iAmOperate = '';
					if(order.so_status == '1'){
						soStatus = '未完成';
						if (soType == 1)//已抢订单服务
							iAmOperate = '<button class="btn-done-order" onclick="doneRobedOrder(\''+order.so_bill_number+'\',1,\'Y\',this)">完成</button>';
					}else if (order.so_status == '2') {
						soStatus = '已完成';
						iAmOperate = '<button class="btn-done-order" onclick="rateUser(\''+order.so_bill_number+'\')">评价</button>';
					}else if (order.so_status == '3') {
						soStatus = '已取消';
					}else if (order.so_status == '4') {
						soStatus = '待确认';
						if (soType == 0)//已发布订单服务
							iAmOperate = '<button class="btn-done-order" onclick="doneRobedOrder(\''+order.so_bill_number+'\',0,\'Y\',this)">完成</button>';
					}else if (order.so_status == '0') {
						soStatus = '未被抢';
						iAmOperate = '<button class="btn-done-order" onclick="doneRobedOrder(\''+order.so_bill_number+'\',0,\'N\',this)">取消</button>';
					}
					appendStr += '<td>'+soStatus+'</td><td>'+iAmOperate+'</td></tr>';//class="fixed-column"
				}
				
				//添加分页工具栏
				//$('.wp-paginate').children().remove();
				var serviceOrderTotalResult = parseInt(result.serviceOrderTotalResult);//总记录数
				var serviceOrderTotalPage = parseInt(result.serviceOrderTotalPage);//总页数
				var previousPageCss = ' nohave';
				if (currentPage > 1) {
					previousPageCss = ' have';
				}
				var pageAppendStr = '<li><a href="javascript:void(0)" class="first'+previousPageCss+'">首页</a></li>'+
			        '<li><a href="javascript:void(0)" class="previous'+previousPageCss+'">&lt;</a></li>';
				if (serviceOrderTotalPage > 0) {
					////页面要显示的页数集合
					var pagingNumber = serviceOrderTotalPage > 10 ? 10 : serviceOrderTotalPage;
					var k = 0;
					//保证当前页为中间页
					for (var g = 0; g < serviceOrderTotalResult; g++) {
						var currentPageCss = (g + 1) == currentPage ? ' current' : ' have';
						if((g >= currentPage - (pagingNumber / 2 + 1) || g >= serviceOrderTotalPage - pagingNumber) && k < pagingNumber){
							pageAppendStr += '<li><a href="javascript:void(0)" class="page'+currentPageCss+'">'+(g+1)+'</a></li>';
							k ++;
						}else if (k >= pagingNumber) {
							break;
						}
					}
				}
				previousPageCss = ' have';
				if (currentPage >= serviceOrderTotalPage) {
					previousPageCss = ' nohave';
				}
				pageAppendStr += '<li><a href="javascript:void(0)" class="next'+previousPageCss+'">&gt;</a></li>'+
							'<li><a href="javascript:void(0)" class="last'+previousPageCss+'">尾页</a></li>';
				
				if (soType == 0) {//发布服务
					$('#myIssueServiceOrderTotalCount').text(totalRows);
					$('#myIssueServiceOrderTotalAmount').text(totalAmount.toFixed(2));
					$('#issueServiceOrderDetail tbody').children().remove();
					$('#issueServiceOrderDetail tbody').append(appendStr);
					$('#issueServicePage').children().remove();
					$('#issueServicePage').append(pageAppendStr);
				}else {
					$('#myRobServiceOrderTotalCount').text(totalRows);
					$('#myRobServiceOrderTotalAmount').text(totalAmount.toFixed(2));
					$('#robServiceOrderDetail tbody').children().remove();
					$('#robServiceOrderDetail tbody').append(appendStr);
					$('#robServicePage').children().remove();
					$('#robServicePage').append(pageAppendStr);
				}
			}else if(result.status == '1') {
				alert('登录已过期，请重新登录');
				sessionStorage.removeItem(SCS_USER_NAME);
			}
			
			$(".wp-paginate a").click(function() {
				var className = this.className;
				if (className.indexOf('nohave') == -1 && parseInt(this.innerText) != currentPage) {
					if(className.indexOf('first') > -1)
						currentPage = 1;
					else if(className.indexOf('previous') > -1)
						currentPage = currentPage > 1 ? currentPage - 1 : 1;
					else if(className.indexOf('next') > -1)
						currentPage = currentPage < serviceOrderTotalPage ? currentPage + 1 : serviceOrderTotalPage;
					else if(className.indexOf('last') > -1)
						currentPage = serviceOrderTotalPage;
					else 
						currentPage = parseInt(this.innerText);
					queryMyServiceOrderData(currentPage, pageLimit,queryServiceOrderStatus);
				}
			});
			var footerBottom = $('body').height() - $('#footer').offset().top - 154;
			if (footerBottom > 2) {
				$('#footer').css({'bottom':'0','position':'fixed'});
			}
		},
		error: function(result) {
			showErrorMsg(result);
		}
	});
}
//获取状态
function getStatus(type) {
	if (type == 'issueAllStatusServiceOrder' || type == 'robAllStatusServiceOrder') 
		return '';
	else if (type == 'issueWaitPayServiceOrder' || type == 'robWaitPayServiceOrder') 
		return '4';
	else if (type == 'issueWaitServiceServiceOrder' || type == 'robWaitServiceServiceOrder') 
		return '5';
	else if (type == 'issueWaitEvaluateServiceOrder' || type == 'robWaitEvaluateServiceOrder') 
		return '6';
	else if (type == 'issueCompleteServiceOrder' || type == 'robCompleteServiceOrder') 
		return '2';
	return '0';
}
//查询交易日志
function queryMyDealLog(currentPage, pageSize, dealType) {
	var startAmount = $('#startDealMoney').val(), dealDate = $('#myDealLogDate').val(), 
	endAmount = $('#endDealMoney').val();
	if (isNull(dealDate)) {
		alert('日期不能为空');
		return;
	}
	if(!isNull(startAmount) && isNaN(startAmount)){
		alert('开始金额只能是数字');
		return;
	}
	if(!isNull(endAmount) && isNaN(endAmount)){
		alert('结束金额只能是数字');
		return;
	}
	$.ajax({
		type: 'post',
		url: SCS_INDEX_URL + 'happuser/queryMyDealLog',
		dataType: 'json',
		async: false,
		timeout: 30000,
		data: {
			DEAL_TYPE: dealType,
			DEALMONTH: dealDate,
			S_AMOUNT: startAmount,
			currentPage: currentPage,
			showCount: pageSize,
			E_AMOUNT: endAmount
		},
		success: function(result) {
			if (result.status == '0') {
				//服务订单数据
				var appendStr = '';
				var dealArray = result.udList, payTotalAmount = 0, rechargeTotalAmount = 0;
				for (var s = 0; s < dealArray.length; s++) {
					var myDeal = dealArray[s];
					var typeStr = '充值';
					if (myDeal.DTYPE == '1') {
						typeStr = '支出';
						payTotalAmount += parseFloat(myDeal.DAMOUNT);
					}else {
						rechargeTotalAmount += parseFloat(myDeal.DAMOUNT);
					}
					appendStr += '<tr>' +
				                    '<td>'+myDeal.CREATEDATE+'</td>' +
				                    '<td>'+typeStr+'</td>' +
				                    '<td>'+parseFloat(myDeal.DAMOUNT).toFixed(2)+'</td>' +
				                    '<td>'+myDeal.DREMARK+'</td>' +
				                '</tr>';
				}
				
				//添加分页工具栏
				var userDealTotalResult = parseInt(result.userDealTotalResult);//总记录数
				var userDealTotalPage = parseInt(result.userDealTotalPage);//总页数
				var previousPageCss = ' nohave';
				if (currentPage > 1) {
					previousPageCss = ' have';
				}
				var pageAppendStr = '<li><a href="javascript:void(0)" class="first'+previousPageCss+'">首页</a></li>'+
			        '<li><a href="javascript:void(0)" class="previous'+previousPageCss+'">&lt;</a></li>';
				if (userDealTotalPage > 0) {
					////页面要显示的页数集合
					var pagingNumber = userDealTotalPage > 10 ? 10 : userDealTotalPage;
					var k = 0;
					//保证当前页为中间页
					for (var g = 0; g < userDealTotalResult; g++) {
						var currentPageCss = (g + 1) == currentPage ? ' current' : ' have';
						if((g >= currentPage - (pagingNumber / 2 + 1) || g >= userDealTotalPage - pagingNumber) && k < pagingNumber){
							pageAppendStr += '<li><a href="javascript:void(0)" class="page'+currentPageCss+'">'+(g+1)+'</a></li>';
							k ++;
						}else if (k >= pagingNumber) {
							break;
						}
					}
				}
				previousPageCss = ' have';
				if (currentPage >= userDealTotalPage) {
					previousPageCss = ' nohave';
				}
				pageAppendStr += '<li><a href="javascript:void(0)" class="next'+previousPageCss+'">&gt;</a></li>'+
							'<li><a href="javascript:void(0)" class="last'+previousPageCss+'">尾页</a></li>';

				$('#payMoneyTotalAmount').text(payTotalAmount.toFixed(2));
				$('#rechargeMoneyTotalAmount').text(rechargeTotalAmount.toFixed(2));
				$('#myDealDetail tbody').children().remove();
				$('#myDealDetail tbody').append(appendStr);
				$('#dealDetailPage').children().remove();
				$('#dealDetailPage').append(pageAppendStr);
			}else if(result.status == '1') {
				alert('登录已过期，请重新登录');
				sessionStorage.removeItem(SCS_USER_NAME);
			}
			
			$(".wp-paginate a").click(function() {
				var className = this.className;
				if (className.indexOf('nohave') == -1 && parseInt(this.innerText) != currentPage) {
					if(className.indexOf('first') > -1)
						currentPage = 1;
					else if(className.indexOf('previous') > -1)
						currentPage = currentPage > 1 ? currentPage - 1 : 1;
					else if(className.indexOf('next') > -1)
						currentPage = currentPage < userDealTotalPage ? currentPage + 1 : userDealTotalPage;
					else if(className.indexOf('last') > -1)
						currentPage = userDealTotalPage;
					else 
						currentPage = parseInt(this.innerText);
					queryMyDealLog(currentPage, pageLimit, getDealType(''));
				}
			});
		},
		error: function(result) {
			//showErrorMsg(result);
			var sessionStatus = result.getResponseHeader("session-status");
			if(sessionStatus != 'timeout'){//如果不是登录超时错误
				showLglTipsBox(300,70,'系统提示','当前系统繁忙，请稍候再试',false);
			}
		}
	});
}
/**
 * 显示错误信息
 * @param result
 * @returns
 */
function showErrorMsg(result) {
	showLglTipsBox(300,70,'系统提示','当前系统繁忙，请稍候再试',false);
}
//获取交易类型
function getDealType(id) {
	var myDealType = '';
	if (isNull(id)) {
		$('#dealType span').each(function(index) {
			if (this.className == 'deal-type-selected') {
				if(this.id == 'rechargeDealType'){
					myDealType = '1';
				}else if(this.id == 'payDealType'){
					myDealType = '2';
				}else {
					myDealType = '';
				}
			}
		});
	}else {
		if (id == 'rechargeDealType') {
			myDealType = 2;
		}else if (id == 'payDealType') {
			myDealType = 1;
		}else {
			myDealType = '';
		}
	}
	return myDealType;
}
//查询交易日志
function queryMyMakeMoney() {
	$.ajax({
		type: 'post',
		url: SCS_INDEX_URL + 'serviceOrder/myMakeMoney',
		dataType: 'json',
		async: false,
		timeout: 30000,
		success: function(result) {
			if (result.status == '0') {
				//服务订单数据
				$('.roll-word').text('我  已  经  赚  了  ' + result.totalMakeMoney + '  元');
			}else if(result.status == '1') {
				alert('登录已过期，请重新登录');
				sessionStorage.removeItem(SCS_USER_NAME);
			}
		},
		error: function(result) {
			var sessionStatus = result.getResponseHeader("session-status");
			if(sessionStatus != 'timeout'){//如果不是登录超时错误
				showLglTipsBox(300,70,'系统提示','当前系统繁忙，请稍候再试',false);
			}
		}
	});
}
/**
 * 完成已抢订单
 * @param robedBillNo
 * @param robOrIssue  是我的订单表格还是已抢订单表格传过来的，0:发布，1：已抢
 * @param isFinish  是否为完成按钮标识，Y:是完成按钮，N：取消按钮
 * @returns
 */
function doneRobedOrder(robedBillNo,robOrIssue,isFinish,btnObj) {
	var confirmMsg = '您确认已完成本单任务了吗？';
	if(isFinish == 'N')
		confirmMsg = '取消操作将会影响您在本平台的信誉等级，您确认要取消吗？';
	if(!confirm(confirmMsg))
		return;
	btnObj.setAttribute("disabled", true);
	btnObj.style.backgroundColor = '#cccccc';
	$.ajax({
		type: 'post',
		url: SCS_INDEX_URL + 'serviceOrder/editServiceOrder',
		dataType: 'json',
		data: {
			isFinish: isFinish,
			robOrIssue: robOrIssue,//0:发布，1：已抢
			billNumber: robedBillNo
		},
		timeout: 30000,
		success: function(result) {
			if (result.status == '0') {
				if(robOrIssue == '1')
					alert('处理订单完成成功，但仍需对方确认');
				else if(robOrIssue == '0' && isFinish == 'Y')
					alert('处理订单完成成功，快去评论一下人家吧');
				else 
					alert('处理订单完成成功');
				var statusText = '';
				if(robOrIssue == '0' && isFinish == 'N'){
					statusText = '已取消';
				}else if(robOrIssue == '0' && isFinish == 'Y'){
					statusText = '已完成';
				}else if(robOrIssue == '1' && isFinish == 'Y'){
					statusText = '待确认';
				}
				var btnObjTd = $(btnObj).parent('td');
				btnObjTd.prev().html(statusText);
				btnObj.remove();
				//如果是完成操作，则添加评价按钮
				if(robOrIssue == '0' && isFinish == 'Y'){
					btnObjTd.html('<button class="btn-done-order" onclick="rateUser(\''+robedBillNo+'\')">评价</button>');
				}
			}else if(result.status == '1') {
				alert('处理订单完成时失败');
				btnObj.style.backgroundColor = '#ffa12d';
			}
			btnObj.setAttribute("disabled", false);
		},
		error: function(result) {
			alert('当前系统繁忙，请稍候再试');
			btnObj.setAttribute("disabled", true);
			btnObj.style.backgroundColor = '#ffa12d';
		}
	});
}
/**
 * 评价用户
 * @param soBillNo
 * @returns
 */
function rateUser(soBillNo) {
	
}