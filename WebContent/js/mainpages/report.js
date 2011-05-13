var chart;
var xml;

function createXML(table){
	
	var groups = [];
	// the group names
	$('thead td', table).each( function(i) {
		groups.push(this.innerHTML);
	});
	
	// the info into xml form
	var xmlform = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
	  "<!DOCTYPE reservationlist SYSTEM \"C:\Users\Peter\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\s7355342andspeter\\xslt\labby.dtd\">\n";
	xmlform+="<RESERVATIONLIST>\n";
    $('tbody tr', table).each( function(i) {
        var tr = this;
        var instrumentId;
        var instrumentType;
	    $('th, td', tr).each( function(j) {
			if (j==0){ //create instrument information
				var last = this.innerHTML.lastIndexOf("-");
				instrumentId = this.innerHTML.slice(last+1);
				instrumentType = this.innerHTML.slice(0,last);
		    }
		    else if (j>1){
		    	xmlform += "\t<RESERVATION>\n";
		    	xmlform += "\t\t<INSTRUMENTID>"+instrumentId+"</INSTRUMENTID>\n";
				xmlform += "\t\t<INSTRUMENTTYPE>"+instrumentType+"</INSTRUMENTTYPE>\n";
		    	xmlform += "\t\t<GROUP>"+groups[j-2]+"</GROUP>\n";
				xmlform += "\t\t<TIMESLOTS>"+this.innerHTML+"</TIMESLOTS>\n";
				xmlform += "\t</RESERVATION>\n";
		    }
	    });
	});
    xmlform += "</RESERVATIONLIST>\n";
    return xmlParse(xmlform);
}

function  barchart(table) {

	var options = {
			chart: {
				renderTo: 'container',
				defaultSeriesType: 'column',
        		backgroundColor: '#f6f6f6',
        		borderWidth: 2
     		},
     		title: {
     		   text: 'Instrument usage per group'
     		},
     		xAxis: {
	 			title: {
	         		text: 'Instrument name'
     	 		}
     		},
     		yAxis: {
        		title: {
					text: 'No. of reservations'
        		}
     		},
     		tooltip: {
        		formatter: function() {
					return '<b>'+ this.series.name +'</b><br/>'+
            			this.y +' '+ this.x.toLowerCase();
        		}
			},
    		credits: {
				enabled:  false
    		}
		};
	  
	   // the categories
	   options.xAxis.categories = [];
	   $('tbody th', table).each( function(i) {
	      options.xAxis.categories.push(this.innerHTML);
	   });
	   
	   // the data series
	   options.series = [];
	   $('tr', table).each( function(i) {
	      var tr = this;
	      $('th, td', tr).each( function(j) {
	         if (j > 0) { // skip first column
	            if (i == 0) { // get the name and init the series
	               options.series[j - 1] = { 
	                  name: this.innerHTML,
	                  data: []
	               };
	            } else { // add values
	               options.series[j - 1].data.push(parseFloat(this.innerHTML));
	            }
	         }
	      });
	   }),
	   chart = new Highcharts.Chart(options);
	}

function piechart(instrument_name,table){

	var options = {
		      chart: {
        renderTo: 'container',
        margin: [50, 200, 60, 170],
        backgroundColor: '#f6f6f6',
        borderWidth: 2
     },
     title: {
        text: 'Group usage of instrument ' + instrument_name
     },
     plotArea: {
        shadow: null,
        borderWidth: null,
        backgroundColor: null
     },
     tooltip: {
        formatter: function() {
           return '<b>'+ this.point.name +'</b>: '+ this.y +' %';
        }
     },
     credits: {
			enabled:  false
     },
     plotOptions: {
        pie: {
           allowPointSelect: true,
           cursor: 'pointer',
           dataLabels: {
              enabled: true,
              formatter: function() {
                 if (this.y > 5) return this.point.name;
              },
              color: 'white',
              style: {
                 font: '13px Trebuchet MS, Verdana, sans-serif'
              }
           }
        }
     },
     legend: {
         layout: 'vertical',
         style: {
            left: 'auto',
            bottom: 'auto',
            right: '50px',
            top: '100px'
         }
      },
      series: [{
          type: 'pie',
          name: 'Group usage',
          data:[]
       }]
     };

	// the group names
	$('thead td', table).each( function(i) {
		var tmp = [this.innerHTML];
		options.series[0].data.push(tmp);
	});

	// Get the sum
	var total = 0;
    // the data series
    $('tbody tr', table).each( function(i) {
        var tr = this;
        var flag = true;
	    $('th, td', tr).each( function(j) {
			if (j==0){ //check for the right instrument
				if (this.innerHTML!=instrument_name){
					flag=false;
			    }
				else {
					flag = true;
				}
		    }
		    else if(flag){ // add values
			    if (j==1){
				    total = parseInt(this.innerHTML);
			    }
			    else {
			    	options.series[0].data[j-2].push(parseFloat(((100*parseFloat(this.innerHTML))/total).toFixed(1)));
			    }
		    }
	    });
	}),

	chart = new Highcharts.Chart(options);
}

function onStart(){
	xml=createXML(document.getElementById('datatable'));
	onSelection();
}

function onSelection(){
	 var inst = document.getElementById("inst_selection").value;
	 if (inst == "all"){
		 barchart(document.getElementById('datatable'));
	 }
	 else {
		 piechart(inst,document.getElementById('datatable'));
	 }
	 
}

function move() {
	if (document.xsltform.xslt_select.value == "XSL1") {
		test_xslt("xslt/reservations1.xsl");
	}
	else if (document.xsltform.xslt_select.value ==  "XSL2" ) {
		test_xslt("xslt/reservations2.xsl");
	}
	else if (document.xsltform.xslt_select.value == "custom" ) {
		var data = document.getElementById("upload").files.item(0).getAsBinary();
		try {
			test_xslt2(data);
		} catch (e){
			alert("Unsupported file");
		}
	}
	else if (document.xsltform.xslt_select.value == "showDTD" ) {
		window.location.href='xslt/labby.dtd';
		}
}
function checkForInput() {
	if (document.xsltform.xslt_select.value == "custom") {
		document.xsltform.xsluploadinput.style.display = "inline";
	}
	else {
		 document.xsltform.xsluploadinput.style.display = "none";
	}
}
