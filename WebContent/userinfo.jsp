<%@ page language="java" import="utilities.PageUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Personal info</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery.editinplace.js"></script>
<script type="text/javascript" src="js/mainpages/userinfo.js"  ></script>
</head>
<body>
<div class="main">
  <div class="main_resize">
    <div class="main_left">
      <div class="logo"><a href="main.jsp" ><img src="images/app_name_header2.png" border="0" alt="Labby - the tool for lab managers!" /></a></div>
      <div class="clr"></div>
      <div class="menu">
        <ul>
        	<li><a href="search.jsp"><span>Search Instruments</span></a></li>
        	<li><a href="reserve.jsp"><span>Reserve Instrument</span></a></li>
        	<li><a href="deleteReservation.jsp"><span>Delete reservation</span></a></li>
          	<li><a href="users.jsp"><span>See all users</span></a></li>
          	<% 
          		if (request.getSession().getAttribute("userRole") != null){
          			if(request.getSession().getAttribute("userRole").toString().equals("manager")) {
          				out.print("<li><a href=\"addInstrument.jsp\"><span>Add an Instrument</span></a></li>");
          				out.print("<li><a href=\"instruments.jsp\"><span>Instruments</span></a></li>");
          				out.print("<li><a href=\"addGroup.jsp\"><span>Add group</span></a></li>");
          				out.print("<li><a href=\"editUsers.jsp\"><span>Edit users</span></a></li>");
          				out.print("<li><a href=\"report.jsp\"><span>Produce Report</span></a></li>");
              		}
          		}
          	%>
	        <li><a href="userinfo.jsp" class="active"><span>My Info</span></a></li>
	        <li><a href="logout"><span>Logout</span></a></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
    <div class="main_right">
      <div class="logged" >
		    <%
		    	PageUtils.printLoggedInAs(out,request.getSession());
		    %>
     	</div>
     <div class="clr"></div>
      <h2 style="padding:0px 0 0px 5px;">My information<br /></h2>
      <div id="responseid"><br/></div>
   		<%
   			PageUtils.createUserInfoTable(out,request, response);
   		%>
   		<form action="" method="get" >
   		<input value="Change password" type="submit" class="button" id="add" />
   		</form>
   		<div id="results" >
		</div>
		
		<div id="save_form" style="display:none" >
		<form name="input" action="changePassword" method="get" onsubmit="return check_fields()" >
   			<table>
   				<tr>
   					<td>New password:</td>
   					<td><input type="password" name="password"  maxlength="16" size="20" /></td>
   					<td id="password_field"></td>
   				</tr>
   				<tr>
   					<td>Repeat New password:</td>
   					<td><input type="password" name="password2"  maxlength="16" size="20" /></td>
   					<td id="password2_field"></td>
   				</tr>
   				<tr>
   					<td></td>
   					<td><input value="Change" type="submit" /></td>
   				</tr>
   				<tr>
   					<td></td>
   					<td><input value="Cancel" type="button" class="button" id="cancel" /></td>
   				</tr>
   			</table>
   		</form>
		</div>
		<div id="update" >
		</div>
    </div>
    <div class="clr"></div>
  </div>

</div>
</body>
</html>
