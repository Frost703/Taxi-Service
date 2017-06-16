$(document).ready(function addHandler(){
	
$("#cab").on({click:callCab});
$(".cancel").on({click:cancelRequest});
$(".feedback").on({click:leaveFeedback});
$(".direct").on({click:directCabRequest});
$(".chat").on({click:openChatWithDriver});

loadUserInformation();
getRequestHistory();
});

function loadUserInformation(){
    $.ajax({
            type: "GET",
            url: "http://localhost:8080/user/info",
            data: "token="+localStorage.getItem("token"),
            success: function(data){
    			alert(data);
    			if(isValidToken(data)) {
                    var name = $("#inputName").val(data.name);
                    var phone = $("#inputPhone").val(data.phone);
                    var address = $("#inputAddress").val(data.address);
                }
            }

        });
}

//empty html5 storage with user's token
function emptyStorage(){
	localStorage.removeItem("token");
	alert("removed");
}

//1.1
function callCab(){
    var name = validateInput($("#inputName").val());
    var phone = validateInput($("#inputPhone").val());
    var address = validateInput($("#inputAddress").val());
    var carClass = validateInput($("#inputClass").val());
    var additional = validateInput($("#inputAdditional").val());

    var data = "name="+name+"&phone="+phone+"&address="+address+"&car="+carClass+"&info="+additional;

	$.ajax({
        type: "POST",
        url: "http://localhost:8080/user/call",
        data: data+"&token="+localStorage.getItem("token"),
        success: function(data){
			alert(data);
			if(isValidToken(data)) {
			    if(data.includes("success")) getRequestHistory();
			}
        }

    });
}

//2.1
function getRequestHistory(){
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/user/history",
        data: "token="+localStorage.getItem("token"),
        success: function(data){
			alert(data);
			if(isValidToken(data)) {
			    displayRequestHistory(data);
			}
        }

    });
}

//2.2
                                    function displayRequestHistory(data){
                                        var html = "history data here";
                                        for(int i=0; i < data.length; i++){
                                            html+= data[i];
                                        }
                                        $("#history").html(html);
                                    }

//3.1 Not for prototype edition
function getAvailableDrivers(){
    $.ajax({
            type: "GET",
            url: "http://localhost:8080/user/available",
            data: "token="+localStorage.getItem("token"),
            success: function(data){
    			alert(data);
    			if(isValidToken(data)) {
    			    displayAvailableDrivers(data);
                }
            }
        });
}

//3.2
                                        function displayAvailableDrivers(data){
                                            var html = "available drivers here";
                                            for(int i=0; i < data.length; i++){
                                                html+= data[i];
                                            }
                                            #("#availableDrivers").html(html);
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

function isValidToken(data){
    if(data.includes("Token not recognized")) {
        promptToLogin();
        return false;
    }
    else return true;
}

function promptToLogin(){
    alert("Session expired. Please, press OK to login to your account again");
    //Prompt user, not alert. Redirect user to login page
    emptyStorage();
}

function validateInput(string){
    var symbols = ['#','$','%','^','&','_','/','\\','|','',';',':'];
    for(i=0; i<symbols.length; i++) string = string.replace(symbols[i], "");
    return string;
}