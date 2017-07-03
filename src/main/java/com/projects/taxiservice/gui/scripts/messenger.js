var refreshRate = 3;
$(document).ready(function addHandler(){
checkAvailableMessages();
});

function checkAvailableMessages(){
    getMessages();
    setTimeout(getMessages, refreshRate*1000);
}

function getMessages(){
alert("Getting messages");
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/messenger/message",
        data: "token="+localStorage.getItem("token"),
        success: function(data){
            if(isValidToken(data)) {
                displayMessages(data);
            }
        }
    });
}

function displayMessages(messages){
    if(messages == null) return;

    if(typeof messages === 'object'){
        for(i = messages.length - 1; i >= 0; i--) displayMessage(messages[i]);
    }
    else displayMessage(message);
}

function displayMessage(message){
    chat = $(".activeChatWindow");
    html = chat.html();

    mes = '<div class="messageItem">'+message+'</div><br>';
    html += mes;

    chat.html(html);
}

function sendMessage(){
    var id = 0;

    id = $("#activeCustomerName").attr("class");
    alert("Id after #activeCustomerName = " + id);
    if(window.location.href.includes("user")) id = $("#history").closest("tr").find(".driver").find("div").attr("class");

    var message = validateInput($("#messageInput").val());

    var data = "receiver="+id+"&message="+message;
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/messenger/message",
        data: data+"&token="+localStorage.getItem("token"),
        success: function(data){
            if(isValidToken(data)) {
                alert(data);
                displayMessage(data);
            }
        }
    });
}