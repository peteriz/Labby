<%@ page language="java" import="utilities.SessionUtils"%>
<%@ page language="java" import="utilities.PageUtils"%>
<%
	SessionUtils.checkLiveSession(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - user page</title>
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
      <h2 style="padding:0px 0 0px 5px;">Labby main page<br /></h2>
      <p class="grey">
      <% 
      	if (request.getParameter("code") == null) {
      		out.write("Welcome " +request.getSession().getAttribute("fullName")+"! <br/>Have fun using the lab!");
      	} 
      	else if (request.getParameter("code").equals("instok")){
      		out.write("Instrument added successfully<br/>");
      		out.write("The new instrument ID is " + request.getParameter("id"));
      	}
      	else if (request.getParameter("code").equals("groupok")){
      		out.write("Group added successfully<br/>");
      	}
      	else if (request.getParameter("code").equals("reservation_done")){
      		out.write("Reservation successfully added<br/>");
      	}
      %>
      </p>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
