<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="com.baidu.ueditor.ActionEnter" import="com.hjlc.util.Constants"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding( "utf-8" );
	response.setHeader("Content-Type" , "text/html");
	String saveRootPath = Constants.UEDITOR_IMG_SAVE_PATH;//自定义保存目录文件
	String rootPath = application.getRealPath( "/" );
	
	out.write( new ActionEnter( request, saveRootPath, rootPath ).exec() );
	
%>