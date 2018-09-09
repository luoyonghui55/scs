<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/admin/top.jsp"%>
	<style type="text/css">
	#table_report .service-time{width: 70px;}
	</style>
	</head>
<body>
<div class="container-fluid" id="main-container">
<div id="page-content" class="clearfix">
  <div class="row-fluid">
	<div class="row-fluid">
		<!-- 检索 -->
		<form method="post" action="serviceOrder/allList" id="serviceOrderForm">
			<table border='0'>
				<tr>
					<td>
						<span class="input-icon">
							<input autocomplete="off" style="width:150px;" type="text" name="so_name" placeholder="这里输入标题" />
							<i id="nav-search-icon" class="icon-search"></i>
						</span>
					</td>
					<td>
						<span class="input-icon"><!-- id="nav-search-input"  -->
							<input autocomplete="off" style="width:150px;" type="text" name="username" placeholder="下单人姓名或电话" />
							<i id="nav-search-icon" class="icon-search"></i>
						</span>
					</td>
					<td><input class="span10 date-picker" name="startDate" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" style="width:100px;" placeholder="发布开始日期"/></td>
					<td><input class="span10 date-picker" name="endDate" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" style="width:100px;" placeholder="发布截止日期"/></td>
					
					<td style="vertical-align:top;">
					 	<!-- class="chzn-select"  -->
					 	状态:
					 	<select name="so_status" data-placeholder="状态" style="vertical-align:top;width: 100px;">
							<option value="">全部</option>
							<option value="0">未抢</option>
							<option value="1">已抢未完成</option>
							<option value="2">已完成</option>
							<option value="3">已删除</option>
						</select>
					</td>
					<td style="vertical-align:top;">
					 	省市:
					 	<select class="province-select" name="so_province" data-placeholder="省市" style="vertical-align:top;width: 100px;" onchange="changeCity(this.value)">
							<option value="">全部</option>
							<c:forEach items="${provincesList }" var="p" varStatus="ps">
								<option value="${p.provinceid }">${p.province }</option>
							</c:forEach>
						</select>
					</td>
					<td style="vertical-align:top;">
					 	市:
					 	<select id="so_city" name="so_city" data-placeholder="市" style="vertical-align:top;width: 100px;" onchange="changeArea(this.value)">
							<option value="">全部</option>
						</select>
					</td>
					<td style="vertical-align:top;">
					 	区/县:
					 	<select id="so_area" name="so_area" data-placeholder="区/县" style="vertical-align:top;width: 100px;">
							<option value="">全部</option>
						</select>
					</td>
					<td style="vertical-align:top;"><button class="btn btn-mini btn-light" onclick="search();"><i id="nav-search-icon" class="icon-search"></i></button></td>
					<!-- <td style="vertical-align:top;"><a class="btn btn-mini btn-light" onclick="createHtml();" title="生成html"><i id="nav-search-icon" class="icon-cog"></i></a></td> -->
				</tr>
			</table>
			<!-- 检索  -->
			<table id="table_report" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th>订单标题</th>
						<th class="service-time"><i class="icon-time hidden-phone">下单时间</th>
						<th class="service-time"><i class="icon-time hidden-phone">服务时间</th>
						<th>下单人</th>
						<th>下单人电话</th>
						<th>省/市</th>
						<th>城市</th>
						<th>区/县</th>
						<th>详细地址</th>
						<th>服务联系电话</th>
						<th>订单金额</th>
						<th>支付方式</th>
						<th>订单状态</th>
						<th class="center" style="width: 45px;">操作</th>
					</tr>
				</thead>				
				<tbody>
					
				<!-- 开始循环 -->	
				<c:choose>
					<c:when test="${not empty varList}">
						<c:if test="${QX.cha == 1 }">
						<c:forEach items="${varList}" var="var" varStatus="vs">
							<tr>
								<td>
									<c:if test="${fn:length(var.so_name) > 16}">${fn:substring(var.so_name,0,16)} ...</c:if>
									<c:if test="${fn:length(var.so_name) < 17}">${var.so_name}</c:if>
								</td>
								<td class="service-time">${var.so_order_date }</td>
								<td class="service-time">${var.so_service_date }</td>
								<td>${var.USERNAME }</td>
								<td>${var.PHONE }</td>
								<td>${var.province }</td>
								<td>${var.city }</td>
								<td>${var.area }</td>
								<td>${var.so_address }</td>
								<td>${var.so_phone }</td>
								<td>${var.so_amount }</td>
								<td>
									<c:if test="${var.so_payment_way == '0'}">线下支付</c:if>
									<c:if test="${var.so_payment_way == '1'}">微信支付</c:if>
									<c:if test="${var.so_payment_way == '2'}">支付宝支付</c:if>
									<c:if test="${var.so_payment_way == '3'}">银联支付</c:if>
								</td>
								<td>
									<c:if test="${var.so_status == '0'}">未抢</c:if>
									<c:if test="${var.so_status == '1'}">已抢未完成</c:if>
									<c:if test="${var.so_status == '2'}">已完成</c:if>
									<c:if test="${var.so_status == '3'}">已删除</c:if>
								</td>
								<%-- 
								<td>
									<c:if test="${var.recommand == '0' }"><span class="label label-important arrowed-in">未推荐</span></c:if>
									<c:if test="${var.recommand == '1' }"><span class="label label-success arrowed">已推荐</span></c:if>
								</td>
								<td>
									<c:if test="${var.status == '0' }"><span class="label label-important arrowed-in">未发布</span></c:if>
									<c:if test="${var.status == '1' }"><span class="label label-success arrowed">已发布</span></c:if>
								</td> --%>
								<td style="width: 45px;" class="center">
									<a class="btn btn-mini btn-light" onclick="createHtml('${var.so_id }');" title="生成html"><i id="nav-search-icon" class="icon-cog"></i></a>
									<%-- <div class='hidden-phone visible-desktop btn-group'>
										<c:if test="${QX.edit != 1 && QX.del != 1 }">
											<span class="label label-large label-grey arrowed-in-right arrowed-in"><i class="icon-lock" title="无权限"></i></span>
										</c:if>
									</div> --%>
								</td>
							</tr>
						</c:forEach>
						</c:if>
						
						<c:if test="${QX.cha == 0 }">
							<tr>
								<td colspan="100" class="center">您无权查看</td>
							</tr>
						</c:if>
					</c:when>
					<c:otherwise>
						<tr class="main_info">
							<td colspan="100" class="center" >没有相关数据</td>
						</tr>
					</c:otherwise>
				</c:choose>
				</tbody>
			</table>
			<div class="page-header position-relative">
				<table style="width:100%;">
					<tr>
						<td style="vertical-align:top;">
							<div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div>
						</td>
					</tr>
				</table>
			</div>
		</form>
	</div>
 
	<!-- PAGE CONTENT ENDS HERE -->
  </div><!--/row-->
	
</div><!--/#page-content-->
</div><!--/.fluid-container#main-container-->
	<!-- 返回顶部  -->
	<a href="#" id="btn-scroll-up" class="btn btn-small btn-inverse">
		<i class="icon-double-angle-up icon-only"></i>
	</a>
	
	<!-- 引入 -->
	<script type="text/javascript">window.jQuery || document.write("<script src='static/js/jquery-1.9.1.min.js'>\x3C/script>");</script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/ace-elements.min.js"></script>
	<script src="static/js/ace.min.js"></script>
	
	<script type="text/javascript" src="static/js/chosen.jquery.min.js"></script><!-- 单选框 -->
	<script type="text/javascript" src="static/js/bootstrap-datepicker.min.js"></script><!-- 日期框 -->
	<script type="text/javascript" src="static/js/bootbox.min.js"></script><!-- 确认窗口 -->
	<!-- 引入 -->
	
	<script type="text/javascript">
		
		$(top.hangge());
		
		//检索
		function search(){
			$("#serviceOrderForm").submit();
		}
		
		//生成html
		function createHtml(thisSoId){
			bootbox.confirm("确定要生成html?", function(result) {
				if(result) {
					var url = "<%=basePath%>serviceOrder/createHtml.do?soId="+thisSoId+"&tm="+new Date().getTime();
					$.get(url,function(data){
						if(data=="success"){
							alert("成功创建HTML页面");
						}else{
							alert("创建HTML失败");
						}
					});
				}
			});
		}
		//查询城市
		function changeCity(provinceId) {
			if (provinceId != null && provinceId != '') {
				$.ajax({
					type: 'post',
					url: 'address/queryCityList',
					data: {provinceid: provinceId},
					dataType: 'json',
					success: function (result) {
						var cityList = result.cityList;
						var cityStr = '<option value="">全部</option>';
						$("#so_city").empty();
						for (var g = 0; g < cityList.length; g++) {
							cityStr += '<option value="'+cityList[g].cityid+'">'+cityList[g].city+'</option>';
						}
						$('#so_city').append(cityStr);
					},
					error: function () {
						alert('请求城市信息失败，请稍候再试');
					}
				});
			}
		}
		//查询区县
		function changeArea(cityId){
			if (cityId != null && cityId != '') {
				$.ajax({
					type: 'post',
					url: 'address/queryAreaList',
					data: {cityid: cityId},
					dataType: 'json',
					success: function (result) {
						var areaList = result.areaList;
						var areaStr = '<option value="">全部</option>';
						$("#so_area").empty();
						for (var g = 0; g < areaList.length; g++) {
							areaStr += '<option value="'+areaList[g].areaid+'">'+areaList[g].area+'</option>';
						}
						$('#so_area').append(areaStr);
					},
					error: function () {
						alert('请求城市信息失败，请稍候再试');
					}
				});
			}
		}
		$(function() {
			//单选框
			$(".province-select").chosen(); 
			//$(".chzn-select-deselect").chosen({allow_single_deselect:true});
			//日期框
			$('.date-picker').datepicker();
		});
	</script>
</body>
</html>