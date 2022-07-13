function getBillResponse(){
	
	event.preventDefault();
    const data = new FormData(document.getElementById('billingForm'));
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
	json = JSON.parse(this.responseText);
	printed = "";
	for (i in json.billList){
		printed += "<p>" + json.billList[i].ecommerce + ": " + json.billList[i].amount + "€</p>";
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
	printed = "<p>" + json["result"] + "<p>";    
	document.getElementById("feeResponse").innerHTML = printed;
  }
  xhttp.open("POST", "/modifyFee");
  xhttp.send(data);
     }

function getcreatePPResponse(){
	
	event.preventDefault();
    const data = new FormData(document.getElementById('createPPForm'));
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
	json = JSON.parse(this.responseText);
	printed = "<p>" + json["result"] + "<p>";    
	document.getElementById("createPPResponse").innerHTML = printed;
  }
  xhttp.open("POST", "/createPaymentProcessor");
  xhttp.send(data);
     }

function getCreateECResponse(){
	
	event.preventDefault();
    const data = new FormData(document.getElementById('createECForm'));
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
	json = JSON.parse(this.responseText);
	printed = "<p>" + json["result"] + "<p>";    
	document.getElementById("createECResponse").innerHTML = printed;
  }
  xhttp.open("POST", "/createEcommerce");
  xhttp.send(data);
     }