function refill_data  () {
	var fullname = gup('fullname');
	var phonenum = gup('phonenum');
	var email = gup('email');

	while (fullname != fullname.replace("+"," ")){
		fullname = fullname.replace("+"," ");
	}
	document.getElementById("fullname").setAttribute("value", fullname);
	document.getElementById("phonenum").setAttribute("value", phonenum);
	document.getElementById("email").setAttribute("value", email);
}

function gup( name )
{
	name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );
	if( results == null )
		return "";
	else
		return results[1];
}
function clear_fields() {
	document.getElementById("username_field").innerHTML = "";
	document.getElementById("password_field").innerHTML = "";
	document.getElementById("password2_field").innerHTML = "";
	document.getElementById("fullname_field").innerHTML = "";
	document.getElementById("phonenum_field").innerHTML = "";
	document.getElementById("email_field").innerHTML = "";
	document.getElementById("errormsg").innerHTML = "";

}
function check_fields() {
	var checkUsername = document.input.username.value;
	var checkPassword = document.input.password.value;
	var checkPassword2 = document.input.password2.value;
	var checkFullname = document.input.fullname.value;
	var checkPhone = document.input.phone.value;
	var checkEmail = document.input.email.value;
	var retval = true;

	var tmp = document.getElementById("username_field");
	if ((checkUsername.length > 10) || (checkUsername.length < 6 )) {
		tmp.innerHTML = "<div class=\"error\">Username must be between 6 and 10 characters</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

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
	var tmp = document.getElementById("fullname_field");
	if ( (checkFullname.length <1) || (checkFullname.length >50)  ) {
		tmp.innerHTML = "<div class=\"error\">Full name must must be between 1 and 50 charecters</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	var tmp = document.getElementById("phonenum_field");
	var regex = /\d{4,}/;
	if ( checkPhone.match(regex) != checkPhone ) {
		tmp.innerHTML = "<div class=\"error\">Not a valid phone number</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	var tmp = document.getElementById("email_field");
	var regex = /^[a-zA-Z0-9.][a-zA-Z0-9-_\s\.]+@[a-zA-Z0-9-\s].+\.[a-zA-Z]{2,5}$/;
	if ( checkEmail == ""){
		tmp.innerHTML = "";
	}
	else if (checkEmail.match(regex) != checkEmail  ) {
		tmp.innerHTML = "<div class=\"error\">Not a valid E-mail address</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	return retval;
}