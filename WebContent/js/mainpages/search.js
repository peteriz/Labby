
var toShow=0;
var isShowing=0;

function pressEnter(event)  {
	if (event.keyCode == 13) document.getElementById('searchbutton').click();
}
var request;
function submitSearch() {
	request = new XMLHttpRequest();
	toShow += 1;
	var params = "query=" + document.getElementById("searchfield").value + "&toShow="+toShow;
	request.open("POST","search",true);
	request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	request.setRequestHeader("Content-length", params.length);
	request.setRequestHeader("Connection", "close");
	request.onreadystatechange = showResults;
	request.send(params);
	return false;
}
function showResults() {
	if (request.getResponseHeader("toShow") >= isShowing){
		if(request.readyState < 4){
				document.getElementById("waiting").innerHTML = "Loading...";
		  }
		else if ((request.readyState == 4) && (request.status == 200 ) ) {
			document.getElementById("resultsContainer").innerHTML = request.responseText;
			document.getElementById("waiting").innerHTML = "";
			isShowing=parseInt(request.getResponseHeader("toShow"));
		} else {
			document.getElementById("resultsContainer").innerHTML = "Search failed!";	
			document.getElementById("waiting").innerHTML = "";
		}
	}
}

