<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>订单详情</title>
<meta charset="utf-8">
<link rel="shortcut icon" href="../static/images/userIndex/favicon.ico" />
<link type="text/css" rel="stylesheet" href="../static/css/userIndex/common.css">
<style type="text/css">
.middle-main{width: 820px;background: white;margin: 0 auto;padding: 30px 70px 0 70px;}
.middle-main strong{font-size: 24px;}
.middle-main .service-list{margin-top: 20px;}
.service-list div{font-size: 20px;line-height: 32px;}
.service-list label{font-weight: bolder;}
.service-list .desc{display: block;margin-left: 110px;margin-top: -30px;}
.btn-back{
    background: url(../static/images/userIndex/menu-row-tail.gif) center bottom repeat-x;cursor: pointer;
    border: 0; font-size: 22px;width: 120px;color: white;font-weight: bolder;line-height: 36px;margin-top: 60px;
}
</style>
</head>
<body>
<!--header-->
<header id="pageHeader">
    <div class="row-top">
        <div class="main">
            <div class="wrapper top-logo">
                <a href="/scs">LGL</a>
            </div>
            <a style="display: none;" id="IamLogout" href="javascript:void()">退出</a>
        </div>
    </div>
    <div class="menu-row">
        <div class="menu-bg">
            <div class="main">
                <nav class="indent-left">
                    <ul class="menu wrapper">
                        <li><a href="/scs" target="_blank">热门服务</a></li>
                        <li><a href="javascript: void(0);">发布服务</a></li>
                        <li id="Myself">
                        	<a href="../userPages/userRegister.html" class="user-register">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注册</a>
                            <a href="../userPages/userLogin.html" class="user-login">登录&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
                        </li>
                        <li><a href="../userPages/joinUs.html">JOIN US</a></li>
                        <li><a href="../userPages/commonProblems.html">常见问题</a></li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</header>

<!-- 发表服务表单 -->
<div id="middleMain" class="middle-main">
    <strong>服务订单信息</strong>
    <div class="service-list">
        <div>
            <input id="soId" type="hidden" value="${so.soId }">
            <label>服务标题：</label>
            <span>${so.soName }</span>
        </div>
        <div>
            <label>服务时间：</label>
            <span>${so.soServiceDate}</span>
            <%-- <span><fmt:formatDate value="${so.soServiceDate }" pattern="yyyy-MM-dd HH:mm:ss"/></span> --%>
        </div>
        <div>
            <label>服务类型：</label>
            <span>
	            <c:choose>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_JSJG' }">技师技工</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_AB' }">安保</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_CXDG' }">促销导购</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_SCY' }">送餐员</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_MRMF' }">美容美发</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_ZHXZ' }">展会协助</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_DBFJ' }">打包分拣</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_YC' }">演出</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_SHLR' }">审核录入</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_XYDL' }">校园代理</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_JZJJ' }">家政家教</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_LYMT' }">礼仪模特</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_SXS' }">实习生</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_WJDC' }">问卷调查</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_CDPF' }">传单派发</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_YG' }">义工</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_CZ' }">充场</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_FY' }">翻译</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_QT' }">其它</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_DTLF' }">地推拉访</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_FWY' }">服务员</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_HWKF' }">话务客服</c:when>
	            	<c:when test="${so.soServiceType == 'SERVICE_TYPE_DJ' }">代驾</c:when>
	            	<c:otherwise>其它</c:otherwise>
            	</c:choose>
            </span>
        </div>
        <div>
            <label>服务地址：</label>
            <span>${so.soAddress }</span>
        </div>
        <div>
            <label>联系电话：</label>
            <span>${so.soPhone }</span>
        </div>
        <div>
            <label>服务金额：</label>
            <span>${so.soAmount }</span>
        </div>
        <div>
            <label>要求描述：</label>
            <span class="desc">${so.soDesc }</span>
        </div>
    </div><br>
    <button class="btn-back" onclick="history.back();">返回</button>&nbsp;&nbsp;&nbsp;&nbsp;
    <c:if test="${so.soStatus == '0' }">
    	<button id="btnGrabOrder" class="btn-back">我要抢单</button>
    </c:if>
</div>

<!-- 页面尾部 -->
<div id="footer" class="footer">
    <div class="footer_info">
        <div class="footer-care-us" style="margin-left: 0px;">
            <strong>关注微信</strong><br/>
            <!--微信扫一扫<br />-->
            <img src="../static/images/userIndex/weChat.png"/>
        </div>
        <div class="footer-about-us">
            <strong>关于九九</strong><br/>
            <a target="_blank" href="../userPages/aboutUs.html">关于我们</a><br/>
            <a target="_blank" href="../userPages/serviceAim.html">服务宗旨</a><br/>
            <a target="_blank" href="../userPages/feedback.html">意见反馈</a>
        </div>
        <div class="footer-contact-us">
            <strong>联系我们</strong><br/>
            <span>
                客服电话：186-7078-7994<br />
                服务邮箱：ijobvip@163.com<br />
                公司地址：北京丰台区草桥东路
            </span>
        </div>
        <div class="footer-record" style="float: right;">
            京ICP备25697805号-4  京ICP证182661号  Copyright © 2015-2016
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript" src="../static/js/common/commonPage.js"></script>
<script type="text/javascript" src="../static/js/common/commonHeaderPage.js"></script>