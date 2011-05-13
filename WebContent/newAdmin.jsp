<%@ page language="java" import="managers.UserDetailsManager"%>
<%@ page language="java" import="java.util.LinkedList"%>
<%@ page language="java" import="structures.UserDetails"%>
<%@ page language="java" import="java.util.Collections"%>
<%@page import="utilities.PageUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Promote to manager</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/startpages/newadmin.js" ></script>
</head>
<body >
<div class="main">
  <div class="main_resize">
    <div class="main_left">
      <div class="logo"><a href="./"><img src="images/app_name_header2.png" border="0" alt="Labby - the tool for lab managers!" /></a></div>
      <div class="clr"></div>
      <div class="menu">
        <ul>
          <li><a href="index.jsp"><span>Back</span></a></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
    <div class="main_right">
      <div class="logged" >
     	</div>
     <div class="clr"></div>
     <h2 style="padding:0px 0px 0px 5px;">Promote a user to a Lab Manager<br /></h2>
		<%
			
			Object adminInfo = request.getSession().getAttribute("admin");
			if (adminInfo == null ) {
				response.sendRedirect("index.jsp");
			}
			else {
				request.getSession().removeAttribute("admin");
				PageUtils.createPromote(adminInfo.toString().equals("ok"),out,response);
			}
		%>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
