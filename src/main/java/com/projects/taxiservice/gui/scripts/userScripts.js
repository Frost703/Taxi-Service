$(document).ready(function addHandler(){
	
$("#cab").on({click:callCab});
$(".cancel").on({click:cancelRequest});
$(".feedback").on({click:leaveFeedback});
$(".direct").on({click:directCabRequest});
$(".chat").on({click:openChatWithDriver});

});

function emptyStorage(){
	localStorage.removeItem("token");
	alert("removed");
}

function callCab(){	
	$.ajax({
        type: "POST",
        url: "http://localhost:8080/user",
        data: "token="+localStorage.getItem("token"),
        success: function(data){
			alert(data);
        }

    });
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