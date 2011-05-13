$(document).ready(function() {
				$('#tablesearch').dataTable();
			} );		
$(document).ready(function() {
	  $("#datepicker").datepicker({ 
		  changeMonth: true, 
		  dateFormat: 'dd/mm/yy',
		  autoSize: true,   
		  showButtonPanel: true,
		  minDate: new Date() });
	  $("#datepicker").datepicker("setDate",new Date());
	});

	function checkfields() {
		var retval = true;
		var timeslots = document.reserve.num_timeslots.value;
		var regex = /\d{1,3}/;
		var tmp = document.getElementById("timeslots_err");
		if ( (timeslots.match(regex) != timeslots) || (timeslots < 1 ) || (timeslots > 100 ) ) {
			tmp.innerHTML = "Time slots must be written  with numbers between 1-100";
			retval = false;
			}
		else {
			tmp.innerHTML = "";
			}
		return retval;
	}