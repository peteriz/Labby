$(document).ready(function() {


	$("#searchbox").keyup(function(){
		
		$.get("search.php",{query: $("#searchbox").val(), type: "count"}, function(data){
		
			$("#buttontext").html(data + " Results Available");
		
		});
	});
	
	$("#searchbox").keyup(function(event){

		if(event.keyCode == "13")
		{
			getResults();
		}

	});
	
	$("#submitbutton").click(function(){
	
		getResults();
	
	});

	function getResults()
	{
	
		$.get("search.php",{query: $("#searchbox").val(), type: "results"}, function(data){
		
			$("#resultsContainer").html(data);
			$("#resultsContainer").show("blind");
		});
	}

});