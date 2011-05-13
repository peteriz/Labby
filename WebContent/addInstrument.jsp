<%@ page language="java" import="utilities.SessionUtils"%>
<%@ page language="java" import="utilities.PageUtils"%>
<%
	SessionUtils.checkLiveSession(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Add instrument</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/mainpages/addinstrument.js"></script>
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
          				out.print("<li><a href=\"addInstrument.jsp\" class=\"active\" ><span>Add an Instrument</span></a></li>");
          				out.print("<li><a href=\"instruments.jsp\"><span>Instruments</span></a></li>");
          				out.print("<li><a href=\"addGroup.jsp\"><span>Add group</span></a></li>");
          				out.print("<li><a href=\"editUsers.jsp\"><span>Edit users</span></a></li>");
          				out.print("<li><a href=\"report.jsp\"><span>Produce Report</span></a></li>");
              		}
          		}
          	%>
	        <li><a href="userinfo.jsp"><span>My Info</span></a></li>
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
      <h2 style="padding:0px 0 0px 5px;">Add an Instrument<br /></h2>
      	<form name="input" action="createInstrument" method="get" onsubmit="return check_fields()" onreset="clear_fields()">
      	<table>
      		<tr>
      			<td>Type: </td>
      			<td><input type="text" name="type" maxlength="20" size="25" id="type" /></td>
      			<td id="type_field" >
      			<% 
      				String code = request.getParameter("code");
      				if (code != null ) {
      					if (code.equals("type"))  {
      						out.write("<div class=\"error\">Instrument type must me at least 1 character</div>");
      					}
      				}
      			%>
      			</td>
      		</tr>
      		<tr>
      			<td>Permission: </td>
      			<td><input type="text" name="permission" maxlength="3" size="6" id="permission" /></td>
      			<td id="permission_field" >
      			<% 
      				if (code != null ) {
      					if (request.getParameter("code").equals("permission"))  {
      						out.write("<div class=\"error\">Not a valid permission value</div>");
      					}
      				}
      			%>
      			</td>
      		</tr>
      		<tr>
      			<td>Time slot: </td>
      			<td><input type="text" name="timeslot" maxlength="3" size="6" id="timeslot" /></td>
      			<td id="timeslot_field" >
      			<% 
      				if (code != null ) {
      					if (request.getParameter("code").equals("timeslot"))  {
      						out.write("<div class=\"error\">Not a valid time slot length</div>");
      					}
      				}
      			%>
      			</td>
      		</tr>
      		<tr>
      			<td>Description: </td>
      			<td><textarea name="description" rows="3" cols="25" id="description" ></textarea></td>
      			<td id="description_field" >
      			<% 
      				if (code != null ) {
      					if (request.getParameter("code").equals("description"))  {
      						out.write("<div class=\"error\">Description cannot be more than 512 characters</div>");
      					}
      				}
      			%>
      			</td>
      		</tr>
      		<tr>
      			<td></td>
      			<td><input type="submit" value="Submit"  />
      			<input type="reset" /></td>
      		</tr>		
      	</table>
		</form>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
