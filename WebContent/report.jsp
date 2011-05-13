<%@ page language="java" import="utilities.PageUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Show reports</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<link href="css/table_style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/xslt/util.js"></script>
<script type="text/javascript" src="js/xslt/xmltoken.js"></script>
<script type="text/javascript" src="js/xslt/dom.js"></script>
<script type="text/javascript" src="js/xslt/xpath.js"></script>
<script type="text/javascript" src="js/xslt/xslt.js"></script>
<script type="text/javascript" src="js/xslt/xslt_script.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/mainpages/report.js" ></script>
</head>
<body onload="onStart()">
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
          				out.print("<li><a href=\"report.jsp\" class=\"active\"><span>Produce Report</span></a></li>");
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
      <h2 style="padding:0px 0 0px 5px;">View reports</h2>
      <form name="xsltform" method="post" enctype="multipart/form-data" action="">
      Select report to review:
      <select name="xslt_select" onchange="checkForInput()" onkeyup="checkForInput()">
      	<option value="XSL1">Instrument usage XSLT</option>
      	<option value="XSL2">Group usage XSLT</option>
      	<option value="custom">Upload XSL</option>
      	<option value="showDTD">Show DTD</option>
      </select>
      <input type="file" id="upload" name="xsluploadinput" style=" display: none"/>
      <button type="button" onclick="move()">View</button>
      </form>
		<div id="update" >
			<% 
				PageUtils.createReportInfo(out,request,response);
	        %>
	        <br/>
	        <div id="container" style="width: 80%; height: 5em">
			</div>
			<div class="clr"></div>
		</div>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
