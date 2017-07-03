$(document).ready(function addHandler(){

loadUserInformation();
getRequestHistory();

//addButtonHandlers();

});

function addButtonHandlers(){
    //$("#cab").on({click:callCab});
    $(".cancel").on({click:function(){
                        cancelRequest($(this));
                        }
                    });
    $(".feedback").on({click:function(){
                        leaveFeedback($(this));
                        }
                    });
    //$(".direct").on({click:directCabRequest});
    //$(".chat").on({click:openChatWithDriver});
}

function loadUserInformation(){
    $.ajax({
            type: "GET",
            url: "http://localhost:8080/user/info",
            data: "token="+localStorage.getItem("token"),
            success: function(data){
    			if(isValidToken(data)) {
                    $("#inputName").val(data.name);
                    $("#inputPhone").val(data.phone);
                    $("#inputAddress").val(data.address);
                }
            }
        });
}

//empty html5 storage with user's token
function emptyStorage(){
	localStorage.removeItem("token");
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
			if(isValidToken(data)) {
			    displayRequestHistory(data);
			}
        }
    });
}

//2.2
function displayRequestHistory(data){
    var html = "history data here<table>";
    html+="<tr><td>Status</td><td>Requests</td><td>Driver</td><td>Action</td></tr>";
    for(i=0; i < data.length; i++){
        var status = data[i].status;
        var date = data[i].created;
        var hour = date.hour; var minute = date.minute; var second = date.second;
        var driver = data[i].driver.name;
        if(driver == null) driver = "NONE";

        var feedback = data[i].feedback;
        var button = "";
        if(feedback == null) button = status.includes("ACTIVE") ? getButton("cancel") : getButton("feedback");
        html += '<tr class="orders" id="'+data[i].id+'"><td>'+status + "</td><td>" + hour+":"+minute+":"+second + '</td><td class="driver"><div class="'+ data[i].driver.id+'">' + driver+"</div></td><td>"+ button +"</td></tr>";
    }
    html += "</table>";
    $("#history").html(html);
    addButtonHandlers();
}

function getButton(text){
    return '<input type="button" class="'+text+'" value="'+text+'">';
}

//3.1 Not for prototype edition
//function getAvailableDrivers(){
//    $.ajax({
//            type: "GET",
//            url: "http://localhost:8080/user/available",
//            data: "token="+localStorage.getItem("token"),
//            success: function(data){
//    			alert(data);
//    			if(isValidToken(data)) {
//    			    displayAvailableDrivers(data);
//                }
//            }
//        });
//}

//3.2
//                                        function displayAvailableDrivers(data){
//                                            var html = "available drivers here";
//                                            for(int i=0; i < data.length; i++){
//                                                html+= data[i];
//                                            }
//                                            #("#availableDrivers").html(html);
//                                        }


function cancelRequest(elem){
    var id = elem.closest("tr").attr("id");
    if(id > 0) {

        data = "id="+id;
        $.ajax({
                type: "POST",
                url: "http://localhost:8080/user/cancel",
                data: data+"&token="+localStorage.getItem("token"),
                success: function(data){
                    if(isValidToken(data)) {
                        if(data.includes("success")) getRequestHistory();
                    }
                }
            });
    } else alert("Error on cancelling");
}

function leaveFeedback(elem){
    var id = elem.closest("tr").attr("id");
        if(id > 0) {
            var feedback = prompt("Please leave your feedback", "Great service!");
            if(feedback.length > 0) {
                data = "id="+id+"&feedback="+feedback;
                $.ajax({
                        type: "POST",
                        url: "http://localhost:8080/user/feedback",
                        data: data+"&token="+localStorage.getItem("token"),
                        success: function(data){
                            if(isValidToken(data)) {
                                if(data.includes("success")) getRequestHistory();
                            }
                        }
                    });
            }
            } else alert("Error on leaving feedback");
}

//                                        function directCabRequest(){
//                                            alert("Requesting a specific driver");
//                                        }

//                                        function openChatWithDriver(){
//                                            alert("Open chat with a specific driver");
//                                        }

function isValidToken(data){
    if(typeof data === 'object') {
        return true;
    }

    if(data.includes("Token not recognized")) {
        promptToLogin();
        return false;
    }
    else{
        return true;
    }
}

function promptToLogin(){
    if(confirm("Session expired. Please, press OK to login to your account again")) {
        emptyStorage();
        window.location = "/My Java Projects/Taxi Service/src/main/java/com/projects/taxiservice/gui/login.html";
    }
}

function validateInput(string){
    var symbols = ['#','$','%','^','&','_','/','\\','|','\'',';',':'];
    for(i=0; i<symbols.length; i++) string = string.replace(symbols[i], "");
    return string;
}