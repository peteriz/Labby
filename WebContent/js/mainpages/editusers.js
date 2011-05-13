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

