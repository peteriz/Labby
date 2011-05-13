<%@ page language="java" import="utilities.SessionUtils"%>
<%
	 SessionUtils.checkRedirect(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Welcome</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="main">
  <div class="main_resize">
    <div class="main_left">
      <div class="logo"><img src="images/app_name_header2.png" border="0" alt="Labby - the tool for lab managers!" /></div>
      <div class="clr"></div>
      <div class="menu">
        <ul>
          <li><a href="login.jsp"><span>Login</span></a></li>
          <li><a href="forgot.jsp"><span>Forgot password?</span></a></li>
          <li><a href="newUser.jsp"><span>Sign up</span></a></li>
          <li><a href="about.html"><span>About</span></a></li>
        </ul>
      </div>
      <a href="http://www.mozilla.com/en-US/firefox/" target="_blank"  ><img src="images/firefox.jpg" border="0" alt="Get Firefox" /></a><br/>
      <a href="adminLogin.html"  ><img src="images/kgpg.png" alt="Admin login" border="0" width="22" height="22" /></a>
      <div class="clr"></div>
    </div>
    <div class="main_right">
   		<div class="logged" >
		    Not logged in
     	</div>
     <div class="clr"></div>
      <h2 style="padding:0px 0 0px 5px;">Welcome to Labby<br /></h2>
      <p class="grey">Labby is a Scientific-Lab managment application. <br/> 
      in order to use it, you have to be a Lab manager or scientist from the lab. <br/>
      If you do not have a user in the system, please sign-up. If you forgot your password click on <a href="forgot.jsp">Forgot password?</a><br/>
      If you want to be added to a research group, please contact one of the lab managers
      </p>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
