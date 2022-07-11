function getBillResponse(){
	
	event.preventDefault();
    const data = new FormData(document.getElementById('billingForm'));
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
	json = JSON.parse(this.responseText);
	printed = "";
	for (i in json){
		printed += "<p>" + i + ": " + json[i];
		if (!isNaN(json[i])){
			printed += " €";
		}
		printed += "<p>";
	}
    
	document.getElementById("billResponse").innerHTML = printed;
  }
  xhttp.open("POST", "/billingInfo");
  xhttp.send(data);
     }

function getFeeResponse(){
	
	event.preventDefault();
    const data = new FormData(document.getElementById('feeForm'));
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
	json = JSON.parse(this.responseText);
	printed = "<p>" + json["ResultDescription"] + "<p>";    
	document.getElementById("feeResponse").innerHTML = printed;
  }
  xhttp.open("POST", "/modifyFee");
  xhttp.send(data);
     }
