function clear_fields() {
	document.getElementById("type_field").innerHTML = "";
	document.getElementById("permission_field").innerHTML = "";
	document.getElementById("timeslot_field").innerHTML = "";
	document.getElementById("description_field").innerHTML = "";
}
function check_fields() {
	var checkType = document.input.type.value;
	var checkPermission = document.input.permission.value;
	var checkTimeslot = document.input.timeslot.value;
	var checkDescrption = document.input.description.value;
	var retval = true;

	var tmp = document.getElementById("type_field");
	if ((checkType.length < 1 ) || (checkType.length > 20)) {
		tmp.innerHTML = "<div class=\"error\">Instrument type must me at least 1 character</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	var tmp = document.getElementById("permission_field");
	var regex = /\d{1,3}/;
	if ( checkPermission.match(regex) != checkPermission ) {
		tmp.innerHTML = "<div class=\"error\">Not a valid permission value</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	var tmp = document.getElementById("timeslot_field");
	var regex = /\d{1,3}/;
	if ( checkTimeslot.match(regex) != checkTimeslot ) {
		tmp.innerHTML = "<div class=\"error\">Not a valid time slot value</div>";
		retval = false;
	}
	else if (parseInt(checkTimeslot) == 0){
		tmp.innerHTML = "<div class=\"error\">Not a valid time slot value</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	var tmp = document.getElementById("description_field");
	if ( checkDescrption.length > 512) {
		tmp.innerHTML = "<div class=\"error\">Description cannot be more than 512 characters</div>";
		retval = false;
	}
	else {
		tmp.innerHTML = "";
	}

	return retval;
}