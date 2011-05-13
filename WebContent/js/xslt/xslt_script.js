logging = true;
xsltdebug = true;

function el(id) {
	return document.getElementById(id);
}

function loadXMLDoc(dname)
{
if (window.XMLHttpRequest)
  {
  xhttp=new XMLHttpRequest();
  }
else
  {
  xhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
xhttp.open("GET",dname,false);
xhttp.send("");
return xhttp.responseText;
}

function test_xslt(xslfile) {
	var xsltfile = xmlParse(loadXMLDoc(xslfile));
	var html = xsltProcess(xml, xsltfile);
	el('container').innerHTML = html;
}

function test_xslt2(xslfile) {
	
	var xsltfile = xmlParse(xslfile);
	var html = xsltProcess(xml, xsltfile);
	el('container').innerHTML = html;
}

