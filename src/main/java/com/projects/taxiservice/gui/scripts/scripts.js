$(document).ready(function addHandler(){
$(".opt").on({click:function(){
	var type = $("input[name=type]:checked").val();
		if(type == "driver") {
			$(".user").attr("style", "display:none;");
			$(".driver").attr("style", "");
		} else {
			$(".driver").attr("style", "display:none;");
			$(".user").attr("style", "");
		}
}});

$("#showpass").on({click:function(){
	$(this).is(':checked') ? $("#password").attr("type", "text") : $("#password").attr("type", "password");
}});
});


function log_in(){
	var email = $("#login").val();
	var pwd = $("#password").val();
	var type = $("input[name=type]:checked").val();
	
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/login",
        data: "login="+email+"&password="+pwd+"&type="+type,
        success: function(data){
			if(data.includes("wrong")) alert("Wrong login or password!");
			else{				
				localStorage.setItem("token",data);
				if(type.includes("user")){
					window.location="/My Java Projects/Taxi Service/src/main/java/com/projects/taxiservice/gui/user.html";
				} 
				else window.location="/My Java Projects/Taxi Service/src/main/java/com/projects/taxiservice/gui/driver.html";
			}
        }

    });
}	

function register(){
	var email = $("#login").val();
	var pwd = $("#password").val();
	var fullName = $("#name").val();
	var type = $("input[name=type]:checked").val();	
	var data = "login="+email+"&password="+pwd+"&name="+fullName+"&type="+type;
	
	if(type == "user") {
		var phone = $("#phone").val();
		var address = $("#address").val();
		data += "&phone="+phone+"&address="+address;
	} else {
		var since = $("#dsince").val();
		var plate = $("#dplate").val();
		var description = $("#dcar").val();
		var car = $("#dclass").val();
		
		data += "&since="+since+"&plate="+plate+"&description="+description+"&car="+car;
	}
	
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/register",
        data: data,
        success: function(data){
			if(data.includes("success")) window.location = "/My Java Projects/Taxi Service/src/main/java/com/projects/taxiservice/gui/login.html";
			else{	
				alert("Sorry, error on server");
				console.log(data);
			}
        }

    });
}