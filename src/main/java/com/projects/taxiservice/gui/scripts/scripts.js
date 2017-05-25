function login(){
	var email = $("#login").val();
	var pwd = $("#password").val();
	alert("Login: "+email+ " Password: "+pwd);
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/login",
        data: "login="+email+"&password="+pwd,
        success: function(data){
			var obj = data;
			
            alert(obj.name);
			alert(obj.id);
        }

    });
}