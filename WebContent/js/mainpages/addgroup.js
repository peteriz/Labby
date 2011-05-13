function clear_fields() {
	document.getElementById("group_field").innerHTML = "";
}
function check_fields() {
	var checkGroup = document.input.group.value;
	var retval = true;
	
	var tmp = document.getElementById("group_field");
	if ((checkGroup.length < 1 ) || (checkGroup.length > 25)) {
		tmp.innerHTML = "<div class=\"error\">Group name must be between 1 and 25 characters</div>";
		retval = false;
		}
	else {
		tmp.innerHTML = "";
		}
	return retval;
}