$(document).ready(function() {
		$('#tablesearch').dataTable();
} );
function check_fields() {
	if (document.deletion.reservation.value == "") {
		document.getElementById("error_field").innerHTML =  "Enter reservation number to delete";
		return false;
	}
	return true;
}