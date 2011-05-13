function check_fields() {
	var checkUsername = document.input.username.value;
	var checkEmail = document.input.email.value;
	var retval = true;

	var tmp = document.getElementById("username_field");
	if (checkUsername == "" ) {
		tmp.innerHTML = "<div class=\"error\">Username field is blank</div>";
		retval = false;
	}

	var tmp = document.getElementById("email_field");
	var regex = /^[a-zA-Z0-9.][a-zA-Z0-9-_\s\.]+@[a-zA-Z0-9-\s].+\.[a-zA-Z]{2,5}$/;
	if ((checkEmail.match(regex) != checkEmail) || (checkEmail == "")  ) {
		tmp.innerHTML = "<div class=\"error\">Not a valid E-mail address</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}
	return retval;
}