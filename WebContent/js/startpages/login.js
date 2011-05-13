function check_fields() {
	var checkUsername = document.input.username.value;
	var checkPassword = document.input.password.value;
	var retVal = true;
	document.getElementById("error_field").innerHTML = "";
	if (checkUsername == "") {
		document.getElementById("username_field").innerHTML = "<div class=\"error\">Username field cannot be blank</div>";
		retVal = false;
	}
	else {
		document.getElementById("username_field").innerHTML = "";
	}
	if (checkPassword == "") {
		document.getElementById("password_field").innerHTML = "<div class=\"error\">Password field cannot be blank</div>";
		retVal = false;
	}
	else {
		document.getElementById("password_field").innerHTML = "";
	}
	return retVal;
}