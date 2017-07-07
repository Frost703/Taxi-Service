$(document).ready(function addHandler(){

loadDriverInformation();
getActiveOrder();
queryOrders();

//addButtonHandlers();

});

var refreshRate = 5;

function addButtonHandlers(){
    $(".order").on({click:function(){
        acceptOrder($(this));
    }});
}

function loadDriverInformation(){
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/driver/info",
        data: "token="+localStorage.getItem("token"),
        success: function(data){
            if(isValidToken(data)) {
                var obj = JSON.parse(data);
                $("#name").html(obj.name);
                $("#date").html(new Date().toISOString().split('T')[0]);
                $("#orders").html(obj.orders);
            }
        }
    });
}

function statusChange(data){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/driver/status",
        data: data+"&token="+localStorage.getItem("token"),
        success: function(data){
            if(isValidToken(data)) {
                if(data.includes("Already")) alert(data);
            }
        }
    });
}

function onRoute(){
    var id = $(".activeOrder").attr("id");
    data = "id="+id+"&status=onroute";

    statusChange(data);

    $("#activeStatus").html("ON ROUTE");
    refreshRate = 100;
}

function discard(){
    var id = $(".activeOrder").attr("id");
    data = "id="+id+"&status=discard";

    statusChange(data);

    $("#activeStatus").html("");
    $("#activeCustomerName").html("");
    $("#activeCustomerName").attr("class", "0");
    $("#activeCustomerAddress").html("");
    $("#activeCustomerPhone").html("");
    $(".activeOrder").attr("id", "0");

    var orders = $("#orders").html();
    $("#orders").html(orders+1);

    alert("Discarded");
    refreshRate = 5;
    getOrders();
}

function finishOrder(){
    var id = $(".activeOrder").attr("id");
    data = "id="+id+"&status=finished";

    if($("#activeStatus").html().includes("ACCEPTED")) alert("Order must be On Route to finish");
    else{
        statusChange(data);

        $("#activeStatus").html("");
        $("#activeCustomerName").html("");
        $("#activeCustomerName").attr("class", "0");
        $("#activeCustomerAddress").html("");
        $("#activeCustomerPhone").html("");
        $(".activeOrder").attr("id", "0");

        var orders = $("#orders").html();
        $("#orders").html(Number(orders) + 1);

        alert("Finished");
        refreshRate = 5;
        getOrders();
    }
}

function queryOrders(){
    getOrders();
    setTimeout(queryOrders, refreshRate*1000);
}

function getOrders(){
    $.ajax({
            type: "GET",
            url: "http://localhost:8080/driver/orders",
            data: "token="+localStorage.getItem("token"),
            success: function(data){
    			if(isValidToken(data)) {
    			    $("#orderList").html(displayOrders(data));
    			    addButtonHandlers();
    			}
            }
        });
}

function displayOrders(data){
    var html="";

    for(i=0; i<data.length; i++){
        var date = data[i].created;
        var hour = date.hour; var minute = date.minute; var second = date.second;
        var address = data[i].address;
        var username = data[i].name;
        var phone = data[i].phoneNumber;

        html+='<tr id='+data[i].id+'><td class="orderDate">'+hour+':'+minute+':'+second+'</td>'+
                '<td class="customerName">'+username+'</td><td class="customerAddress">'+address+'</td><td class="customerPhone">'+phone+'</td><td><input type="button" value="Take" class="order"></td></tr>\n';
    }
    return html;
}

function acceptOrder(elem){
    var id = elem.closest("tr").attr("id");
    var data = "id="+id;

    if($(".activeOrder").attr("id") < 1) {
        $.ajax({
                type: "POST",
                url: "http://localhost:8080/driver/accept",
                data: data+"&token="+localStorage.getItem("token"),
                success: function(data){
                    if(isValidToken(data)) {
                        if(data.includes("success")) alert(data);

                        getOrders();
                        getActiveOrder();
                    }
                }
        });
    }
}

function getActiveOrder(){
    $.ajax({
            type: "GET",
            url: "http://localhost:8080/driver/active",
            data: "token="+localStorage.getItem("token"),
            success: function(data){
                if(isValidToken(data)) {
                    $(".activeOrder").attr("id", data.id);
                    $("#activeStatus").html(data.status);
                    $("#activeCustomerName").html(data.name);
                    $("#activeCustomerName").attr("class", data.user);
                    $("#activeCustomerAddress").html(data.address);
                    $("#activeCustomerPhone").html(data.phoneNumber);
                    $("#activeCustomerAdditionalInformation").html(data.additionalInformation);
                    $("#activeUserId").val(data.customer.id);
                }
            }
        });
}


//empty html5 storage with driver's token
function emptyStorage(){
	localStorage.removeItem("token");
}

function isValidToken(data){
    if(typeof data === 'object') {
        return true;
    }

    if(data.includes("Token not recognized")) {
        promptToLogin();
        return false;
    }
    else return true;
}

function promptToLogin(){
    if(confirm("Session expired. Please, press OK and login to your account again")) {
        emptyStorage();
        window.location = "/My Java Projects/Taxi Service/src/main/java/com/projects/taxiservice/gui/login.html";
    }
}

function validateInput(string){
    var symbols = ['#','$','%','^','&','_','/','\\','|','\'',';',':'];
    for(i=0; i<symbols.length; i++) string = string.replace(symbols[i], "");
    return string;
}