<%@ page language="java" import="utilities.SessionUtils"%>
<%
	SessionUtils.checkRedirect(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
</head>
<body onload="redirect()">
<script type="text/javascript">
<!-- 
function redirect () { setTimeout("go_now()",5000); }
	function go_now ()   { 
		var localtion = "<%
							String o = request.getParameter("page");
							if (o  == null){
								out.write("index.jsp");
							}
							else {
								out.write(o);
							}
						%>";
		window.location.href = localtion ; 
		}
-->
</script>
<div class="main">
  <div class="main_resize">
    <div class="main_left">
      <div class="logo"><img src="images/app_name_header2.png" alt="Labby - the tool for lab managers!" /></div>
      <div class="clr"></div>
      <div class="menu">
        <ul>
          <li><a href="login.jsp"><span>Login</span></a></li>
          <li><a href="forgot.jsp"><span>Forgot password?</span></a></li>
          <li><a href="newUser.jsp"><span>Sign up</span></a></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
    <div class="main_right">
		<div class="logged" >
		    Not logged in
		</div>
     <div class="clr"></div>
    	 <h1>
    	 	<%
    	 	String code = request.getParameter("code");
			if (code != null) {
      			if (code.equals("e"))  {
					out.write("User added.");
				}
      			else if (code.equals("j")){
      				out.write("User promoted");
      			}
      			else if(code.equals("out")){
      				out.write("You have been logged out");
      			}
      			else if (code.equals("rtrvd")){
      				out.write("Your password has been sent to your email address");
      			}
			}
		 	%>
		</h1>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
