var stompClient = null;
/*var availableRobots = [];
var robot = null;
*/
function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
  /*      stompClient.subscribe('/topic/connected', function (connected) {
            showConnected(JSON.parse(connected.body));
        });*/
        stompClient.subscribe('/topic/mqtt-conf', function (connected) {
            showMqtt(JSON.parse(connected.body));
        });
     //   sendRequestConnected();
        stompClient.send("/app/mqtt-config-get", {}, JSON.stringify());
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}
/*
function sendRequestConnected() {
    stompClient.send("/app/robots", {}, JSON.stringify());
}

function showConnected(message) {
   // console.log(message);
    var val = $("#exampleFormControlSelect1 :selected").text();
    //console.log(val);

    availableRobots = message;
    if ((val == 'No active' && message.length > 0) || val != 'No active') {
        $('#exampleFormControlSelect1')
            .find('option')
            .remove()
            .end();
        //.append('<option value="whatever">text</option>')
        //.val('whatever')
    }
    var currentRobot = null;
    $('#list-connected').empty();
    message.forEach(element => {
       // console.log(element);
        $("#exampleFormControlSelect1").append("<option>" + element.topicId + "</option>");
        if (val == element.topicId) {
            currentRobot = element;
        }
        if (element.status == 'ONLINE') {
            $("#list-connected").append('<li>' + element.topicId + '</li>');

        }
    });

    if (val == 'No active' && message.length > 0) {
      //  console.log(availableRobots[0]);
      //  console.log(availableRobots);
        currentRobot = availableRobots[0];
        localStorage.setItem('robot', currentRobot.topicId);
        $("#exampleFormControlSelect1").val($("#exampleFormControlSelect1 option:first").val());
    } else if (message.length > 0) {
        $("select option").filter(function () {
            //TODO get by value 
            return $(this).text() == val;
        }).prop('selected', true);
    } else {
        return;
    }
    robot = currentRobot;
    switch (currentRobot.status) {
        case 'NO_LINK':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-grey");
            break;
        case 'ONLINE':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-green");
            break;
        case 'ERROR':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-red");
            break;
    }
    //TODO save in cookie chosen
}
*/
function showMqtt(message) {
    $("#mqtt-ip").val(message.ip);
    $("#mqtt-port").val(message.port);
    $("#mqtt-id").val(message.id);
    if(message.connected) {
        $("#mqtt-btn").text("Connected");
        $("#mqtt-btn").removeClass("btn-success btn-danger").addClass("btn-success");
    } else {
        $("#mqtt-btn").text("Not connected");
        $("#mqtt-btn").removeClass("btn-success btn-danger").addClass("btn-danger");
    }

}

function sendBoolean(msg) {
    stompClient.send("/app/cmd-bool", {}, JSON.stringify({'cmd' : msg, 'robot' : robot}));
}

function sendMqttConfig() {
    stompClient.send("/app/mqtt-config", {}, JSON.stringify({'ip' : $("#mqtt-ip").val(), 'id' : $("#mqtt-id").val(),
    'port' : $("#mqtt-port").val(), 'connected' : false}));
}

/*
function newRobot(name) {
    console.log(name);
    availableRobots.forEach(element => {
        if(element.topicId === name){
            robot = element;
            return;
        }
     });
}
*/
function sendMove(msg) {
    stompClient.send("/app/cmd-pos", {}, JSON.stringify({'direction' : msg, 'position' : parseInt($('#position-input').val(), 10), 
    'distances' : [], 'robot': robot}));
}

function sendCargo(type, direction) {
    stompClient.send("/app/cmd-cargo", {}, JSON.stringify({'type' : type, 'direction' :direction, 'robot': robot}));
}


$(function () {
    $("#stop-btn").click(function () { sendBoolean('/action/stop/cmd'); });
    $("#mqtt-btn").click(function () { sendMqttConfig(); });
 /*   $('select').on('change', function() {
        localStorage.setItem('robot', this.value);
        newRobot(this.value);
      });*/
    $("#start-pos-btn").click(function () { sendBoolean('/action/start/cmd'); });
    $("#arrow-up-btn").click(function () {
        $('#position-input').get(0).value++;
    });
    $("#arrow-down-btn").click(function () {
        $('#position-input').get(0).value--;
    });
    $("#move-up-btn").click(function () { sendMove(false); });
    $("#move-down-btn").click(function () { sendMove(true); });
    //first parameter unload - false, load - true. second param: false - left, true - right  
    $("#load-left-btn").click(function () { sendCargo(true, false); });
    $("#load-right-btn").click(function () { sendCargo(true, true); });
    $("#unload-left-btn").click(function () { sendCargo(false, false); });
    $("#unload-right-btn").click(function () { sendCargo(false, true); });

    /*   $("form").on('submit', function (e) {
           e.preventDefault();
       });
       */
    connect();
});