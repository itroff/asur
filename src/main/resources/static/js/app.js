var stompClient = null;
var availableRobots = [];

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/connected', function (connected) {
            showConnected(JSON.parse(connected.body));
        });
        sendRequestConnected();
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendRequestConnected() {
    stompClient.send("/app/robots", {}, JSON.stringify());
}

function showConnected(message) {
    console.log(message);
    var val = $("#exampleFormControlSelect1 :selected").text();
    console.log(val);

    availableRobots = message;
    if ((val == 'No active' && message.length > 0) ||val != 'No active' ) {
        $('#exampleFormControlSelect1')
            .find('option')
            .remove()
            .end();
    //.append('<option value="whatever">text</option>')
    //.val('whatever')
    }
    var currentRobot = null;
    message.forEach(element => {
        console.log(element);
        $("#exampleFormControlSelect1").append("<option>" + element.topicId + "</option>");
        if (val == element.topicId) {
            currentRobot = element;
        }
    });

    if (val == 'No active' && message.length > 0) {
        console.log( availableRobots[0]);
        console.log( availableRobots);
        currentRobot = availableRobots[0];
        $("#exampleFormControlSelect1").val($("#exampleFormControlSelect1 option:first").val());
    } else if (message.length > 0) {
        $("select option").filter(function () {
            //TODO get by value 
            return $(this).text() == val;
        }).prop('selected', true);
    } else {
        return;
    }

    switch (currentRobot.status) {
        case 'NO_LINK':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-gray");
            break;
        case 'ONLINE':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-green");
            break;
        case 'ERROR':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-red");
            break;
    }


        //TODO save in cookie chosen
        //TODO insert list
        //TODO get current
        // $("#exampleFormControlSelect1").append("<tr><td>" + message + "</td></tr>");
    }

    $(function () {
        /*   $("form").on('submit', function (e) {
               e.preventDefault();
           });
           $( "#connect" ).click(function() { connect(); });
           $( "#disconnect" ).click(function() { disconnect(); });
           $( "#send" ).click(function() { sendName(); });
           */
        connect();
    });