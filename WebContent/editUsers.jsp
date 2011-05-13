<%@ page language="java" import="utilities.SessionUtils"%>
<%@ page language="java" import="java.sql.ResultSet"%>
<%@ page language="java" import="utilities.PageUtils"%>
<%@ page language="java" import="managers.GroupsManager"%>
<%@ page language="java" import="java.util.Set"%>
<%@ page language="java" import="java.util.LinkedList"%>
<%@ page language="java" import="java.util.Collections"%>
<%@ page language="java" import="managers.UserDetailsManager"%>
<%@ page language="java" import="structures.UserDetails"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Labby - Edit users info</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery.editinplace.js"></script>

<script type="text/javascript" >
<!-- 
function loadEdit (){
	$("#fullnameid").editInPlace({
		callback: function(unused, enteredText,original) { return prepareAjax("fullname",enteredText,original); },
		show_buttons: true,
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});
	$("#phoneid").editInPlace({
		callback: function(unused, enteredText,original) { return prepareAjax("phonenum",enteredText,original); },
		show_buttons: true,
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});
	$("#addressid").editInPlace({
		callback: function(unused, enteredText,original) {
		var retval= prepareAjax("email",enteredText,original);
		if (retval==""){
			window.location.href = "editUsers.jsp";
		}
		return val; },
		show_buttons: true,
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});
	$("#activeid").editInPlace({
		callback: function(unused, enteredText,original) { return prepareAjax("active",enteredText,original); },
		show_buttons: true,
		field_type: "select",
		select_options: "Yes,No",
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});
	$("#groupid").editInPlace({
		callback: function(unused, enteredText,original) {  return prepareAjax("group",enteredText,original); },
		show_buttons: true,
		field_type: "select",
		select_options: "<% 
			if (SessionUtils.checkLiveSession(request, response)){
				Set<String> grps = GroupsManager.getGroups();
				if (grps == null){
					response.sendError(500,"Server busy");
				}
				else{
					LinkedList<String> groups = new LinkedList<String>(grps);
					Collections.sort(groups);
					String first = "";
					for(String group : groups){
						out.write(first+group);
						first = ",";
					}
					%>",
					bg_over: "#cdcdff",
					saving_text: 'Saving...',
					saving_image: "images/ajaxloader.gif"
				});

			}


		function prepareAjax(fieldName,enteredText,original) {
			var request = new XMLHttpRequest();
			var username = document.userSelectForm.users.value;
			var usernumber = document.getElementById(username).value;
			var params = "function=update&username="+ username +"&usernumber="+usernumber+"&" + fieldName + "=" + enteredText;
			request.open("POST","EditUsers",false);
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			request.setRequestHeader("Content-length", params.length);
			request.setRequestHeader("Connection", "close");
			request.send(params);
			if (request.status==200) {
				printMsg("Updated details");
				return enteredText;
			}
			else {
				printMsg("Failed updating details");
				return original;
			}
		}
		function printMsg(inputText) {
			document.getElementById("responseid").innerHTML = "<p class=\"grey\">" + inputText + "</p>";
		}
		function showUserDetails() {
			var request = new XMLHttpRequest();
			var username = document.userSelectForm.users.value;
			var usernumber = document.getElementById(username).value;
			var params = "function=select&username=" + username+"&usernumber="+usernumber;
			request.open("POST","EditUsers",false);
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			request.setRequestHeader("Content-length", params.length);
			request.setRequestHeader("Connection", "close");
			request.send(params);
			if (request.readyState==4 && request.status==200) {
				var fullname = request.responseXML.getElementsByTagName("FULLNAME");
				document.getElementById("fullnameid").innerHTML = fullname[0].firstChild.nodeValue;
				var groupname = request.responseXML.getElementsByTagName("GROUPNAME");
				document.getElementById("groupid").innerHTML = groupname[0].firstChild.nodeValue;
				var phonenum = request.responseXML.getElementsByTagName("PHONENUM");
				document.getElementById("phoneid").innerHTML = phonenum[0].firstChild.nodeValue;
				var address = request.responseXML.getElementsByTagName("ADDRESS");
				if (address[0].firstChild != null){
					document.getElementById("addressid").innerHTML = address[0].firstChild.nodeValue;
				}
				else {
					document.getElementById("addressid").innerHTML = "";
				}
				var isactive = request.responseXML.getElementsByTagName("ACTIVE");
				if (isactive[0].firstChild.nodeValue == 1) {
					document.getElementById("activeid").innerHTML = "Yes";

				}
				else {
					document.getElementById("activeid").innerHTML = "No";
				}
				var permissions = request.responseXML.getElementsByTagName("PERMISSION");
				var perText = "";
				for (var i = 0; i<permissions.length; ++i) {
					perText +=  permissions[i].firstChild.nodeValue;
					if ((i+1)<permissions.length){ perText += ", ";}
				}
				document.getElementById("permissionid").innerHTML = perText;
			}
			loadEdit();
		}

		function addPer() {
			var request = new XMLHttpRequest();
			var username = document.userSelectForm.users.value;
			var usernumber = document.getElementById(username).value;
			var per = document.getElementById("addper").value;
			var params = "function=addPermission&username=" + username + "&per=" + per+"&usernumber="+usernumber;
			request.open("POST","EditUsers",false);
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			request.setRequestHeader("Content-length", params.length);
			request.setRequestHeader("Connection", "close");
			request.send(params);
			if (request.readyState==4 && request.status==200) {
				showUserDetails();
			}
		}
		function delPer() {
			var request = new XMLHttpRequest();
			var username = document.userSelectForm.users.value;
			var usernumber = document.getElementById(username).value;
			var per = document.getElementById("delper").value;
			var params = "function=delPermission&username=" + username + "&per=" + per+"&usernumber="+usernumber;
			request.open("POST","EditUsers",false);
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			request.setRequestHeader("Content-length", params.length);
			request.setRequestHeader("Connection", "close");
			request.send(params);
			if (request.readyState==4 && request.status==200) {
				showUserDetails();
			}
		}
		function deleteUser(){
			var request = new XMLHttpRequest();
			var username = document.userSelectForm.users.value;
			var usernumber = document.getElementById(username).value;
			var params = "function=delete&username=" + username + "&usernumber="+usernumber;
			request.open("POST","EditUsers",false);
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			request.setRequestHeader("Content-length", params.length);
			request.setRequestHeader("Connection", "close");
			request.send(params);
			if (request.readyState==4 && request.status==200) {
				window.location.href = "editUsers.jsp";
			}
			else {
				printMsg("Failed to delete user");
			}
		}

 -->
</script>

</head>
<body onload="showUserDetails()">
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
          				out.print("<li><a href=\"editUsers.jsp\" class=\"active\"><span>Edit users</span></a></li>");
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
      <h2 style="padding:0px 0 0px 5px;">Edit users<br /></h2>
      <div id="responseid"></div>
      <table>
      <tr>
      	<td><p class="grey">Select user to edit:</p></td>
      	<td>
      	<%
	   			String msg = request.getParameter("code");
	   			if ((msg != null) && (msg.equals("ok"))) {
	   				out.write("<p class=\"error\">user detail changed successfuly!</p>");
	   			}
	   			LinkedList<UserDetails> users = UserDetailsManager.getAllUsers();
	   			if (users == null){
	   				response.sendError(500,"server is busy");
	   			}
	   			else {
	   				Collections.sort(users);
	   				out.write("<form action=\"\" name=\"userSelectForm\" >");
	   				out.write("<select name=\"users\" onchange=\"showUserDetails()\" onkeyup=\"showUserDetails()\" id=\"userselect\" >");
	   				for (UserDetails user : users){
	   					out.write("<option value=\"" + user.getUsername() + "\" >" + user.getUsername()+"</option>");
	   				}
	   				out.write("</select>");
	   				for (UserDetails user : users){
	   					out.write("<input type=\"hidden\" id=\""+user.getUsername()+"\" value=\"" + user.getUsernumber() + "\" />");
	   				}
	   				out.write("</form>");
	   			}
			}
		}
   		%>
   		</td>
      </tr>
    </table>
    <table class="userinfo">
    <tr>
    	<td class="userinfo_h">Fullname:</td>
    	<td id="fullnameid"></td>
    </tr>
    <tr>
    	<td class="userinfo_h">Group name:</td>
    	<td id="groupid"></td>
    </tr>
    <tr>
    	<td class="userinfo_h">Phone num.:</td>
    	<td id="phoneid"></td>
    </tr>
    <tr>
    	<td class="userinfo_h">E-Mail:</td>
    	<td id="addressid"></td>
    </tr>
    <tr>
    	<td class="userinfo_h">Active: </td>
    	<td id="activeid"></td>
    </tr>
    <tr>
    	<td class="userinfo_h">Permissions:</td>
    	<td id="permissionid"></td>
    </tr>
	<tr>
		<td></td>
		<td id="addgroupid"><input type="text" maxlength="4" id="addper" /><button type="button" onclick="addPer()">Add</button></td>
	</tr>
	<tr>
		<td></td>
		<td id="deletegroupid"><input type="text" maxlength="4" id="delper" /><button type="button" onclick="delPer()">Delete</button></td>
	</tr>
	
    </table>
    <button name="deleteUser" type="button" onclick="deleteUser()">Delete user</button>
    </div>
    <div class="clr"></div>
  </div>
</div>
</body>
</html>
