$(document).ready(function(){
	$("#fullname").editInPlace({
		callback: function(unused, enteredText,original) { return prepareAjax("fullname",enteredText,original); },
		show_buttons: true,
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});
	$("#phonenum").editInPlace({
		callback: function(unused, enteredText,original) { return prepareAjax("phonenum",enteredText,original); },
		show_buttons: true,
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});
	$("#email").editInPlace({
		callback: function(unused, enteredText,original) { 
								var tmp = prepareAjax("email",enteredText,original);
								if (tmp == ""){
									window.location.href = "userinfo.jsp";
								}
								return tmp;
							},
		show_buttons: true,
		bg_over: "#cdcdff",
		saving_text: 'Saving...',
		saving_image: "images/ajaxloader.gif"
	});

});


function prepareAjax(fieldName,enteredText,original) {
	var request = new XMLHttpRequest();
	var params = fieldName + "=" + enteredText;
	request.open("POST","UpdateDetail",false);
	request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	request.setRequestHeader("Content-length", params.length);
	request.setRequestHeader("Connection", "close");
	request.send(params);
	if (request.status==200) {
		if (enteredText != ""){
			printMsg("Updated details");
		}
		return enteredText;
	}
	else {
		if (original != ""){
			printMsg("Failed updating details");
		}
		return original;
	}
}

function printMsg(inputText) {
	document.getElementById("responseid").innerHTML = "<p class=\"grey\">" + inputText + "</p>";
}

function check_fields() {
	var checkPassword = document.input.password.value;
	var checkPassword2 = document.input.password2.value;
	var retval = true;
	
	var tmp = document.getElementById("password_field");
	if ( (checkPassword.length <8) || (checkPassword.length>16)) {
		tmp.innerHTML = "<div class=\"error\">Password must be between 8 and 16 characters</div>";
		retval = false;
		}
	else {
		tmp.innerHTML = "";
	}
	
	var tmp = document.getElementById("password2_field");
	if (checkPassword != checkPassword2 ) {
		tmp.innerHTML = "<div class=\"error\">Password and re-entered password do not match</div>";
		retval = false;
		}
	else {
		tmp.innerHTML = "";
		}
	return retval;
}


$(function()
		{

		$(".button").click(function()
		{
		var button_id = $(this).attr("id");

		//Add Record button
		if(button_id=="add")
		{
		$("#results").slideUp("slow");
		$("#save_form").slideDown("slow");
		}

		//Cancel button
		else if(button_id=="cancel")
		{
		$("#save_form").slideUp("slow");
		$("#results").slideDown("slow");
		}

		// save button
		else
		{
		// insert record
		// more details Submit form with jQuery
		}

		return false;
		});
		});

