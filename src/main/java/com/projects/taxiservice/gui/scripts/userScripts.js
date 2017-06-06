$(document).ready(function addHandler(){
	
$("#cab").on({click:callCab});
$(".cancel").on({click:cancelRequest});
$(".feedback").on({click:leaveFeedback});
$(".direct").on({click:directCabRequest});
$(".chat").on({click:openChatWithDriver});

});

function callCab(){
	alert("Call Cab");
}

function cancelRequest(){
	alert("Cancel Request");
}

function leaveFeedback(){
	alert("Leave Feedback");
}

function directCabRequest(){
	alert("Requesting a specific driver");
}

function openChatWithDriver(){
	alert("Open chat with a specific driver");
}