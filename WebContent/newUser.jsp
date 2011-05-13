<%@ page language="java" import="utilities.SessionUtils"%>
<%
	SessionUtils.checkRedirect(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Create a new user</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
</head>
<body onload="refill_data()" >
<script type="text/javascript" src="js/startpages/newuser.js"></script>

<div class="main">
  <div class="main_resize">
    <div class="main_left">
      <div class="logo"><a href="./"><img src="images/app_name_header2.png" border="0" alt="Labby - the tool for lab managers!" /></a></div>
      <div class="clr"></div>
      <div class="menu">
        <ul>
          <li><a href="login.jsp"><span>Login</span></a></li>
          <li><a href="forgot.jsp"><span>Forgot password?</span></a></li>
          <li><a href="newUser.jsp" class="active"><span>Sign up</span></a></li>
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
     <h2 style="padding:0px 0 0px 5px;">Create a new user<br /></h2>
     	<p>Please fill in all required information, fields with * are obligatory.</p>
     	 <p class="error" id="errormsg">
     	 	<%
     	 		String code=request.getParameter("code");
     	 		if(code!=null){
     	 			if (code.equals("d")) {out.write("Error: username already exists in the system.");}
     	 		}
     	 	%>
		</p>
		<form name="input" action="createUser" enctype="multipart/form-data" method="post" onsubmit="return check_fields()" onreset="clear_fields()">
			<table>
			<!-- username form -->
			<tr>
				<td>Username:</td>
				<td><input type="text" name="username" maxlength="10" size="30"/>*</td>
				<td id="username_field"></td>
			</tr>
			<tr>
				<td></td>
				<td>Between 6 and 10 characters</td>
			</tr>
			<!-- password form -->
			<tr>
				<td>Password:</td>
				<td><input type="password" name="password" maxlength="16" size="30"/>*</td>
				<td id="password_field"></td>
			</tr>
			<tr>
				<td></td>
				<td>Between 8 and 16 characters</td>
			</tr>	
			<tr>
				<td>Re-enter password:</td>
				<td><input type="password" name="password2" maxlength="16" size="30"/>*</td>
				<td id="password2_field"></td>
			</tr>
			<!-- full name form -->
			<tr>
				<td>Full name:</td>
				<td><input type="text" name="fullname" maxlength="50" size="30" id="fullname" />*</td>
				<td id="fullname_field"></td>
			</tr>
			<!-- phone number form -->
			<tr>
				<td>Phone number:</td>
				<td><input type="text" name="phone" maxlength="16" size="30" id="phonenum" />*</td>
				<td id="phonenum_field"></td>
			</tr>
			<tr>
				<td></td>
				<td>At least 4 digits</td>
			</tr>
			<!-- email address form -->
			<tr>
				<td>E-mail:</td>
				<td><input type="text" name="email" maxlength="50" size="30" id="email" /></td>
				<td id="email_field"></td>
			</tr>
			</table>
			<input type="submit" value="Submit" />
			<input type="reset" />
		</form>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
