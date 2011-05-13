<%@ page language="java" import="utilities.SessionUtils"%>
<%
	SessionUtils.checkRedirect(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Login</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<script type="text/javascript" src="js/startpages/login.js" ></script>
<div class="main">
  <div class="main_resize">
    <div class="main_left">
      <div class="logo"><a href="./"><img src="images/app_name_header2.png" border="0" alt="Labby - the tool for lab managers!" /></a></div>
      <div class="clr"></div>
      <div class="menu">
        <ul>
          <li><a href="login.jsp" class="active"><span>Login</span></a></li>
          <li><a href="forgot.jsp"><span>Forgot password?</span></a></li>
          <li><a href="newUser.jsp"><span>Sign up</span></a></li>
          <li><a href="about.html"><span>About</span></a></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
    <div class="main_right">
		<div class="logged" >
		    Not logged in
		</div>
     <div class="clr"></div>
      <h2 style="padding:0px 0 0px 5px;">Login to the system<br /></h2>
     	 <p class="error" id="error_field">
     	 	<%
     	 		String code=request.getParameter("code");
     	 		if(code!=null){
     	 			if (code.equals("a")) {out.write("Error: wrong username or password");}
					else if(code.equals("b")) { out.write("Error: user is not active");}
     	 		}
     	 	%>
		</p>
		<form name="input" action="systemLogin" method="get" class="grey" onsubmit="return check_fields()">
			<table>
			<tr>
				<td>Username:</td>
				<td><input type="text" name="username" size="30" /></td>
				<td id="username_field"></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type="password" name="password" size="30" /></td>
				<td id="password_field"></td>
			</tr>
			</table>
			<input type="submit" value="Login" />
		</form>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
