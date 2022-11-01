var stompClient = null;

function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/mqtt-conf', function (connected) {
            showMqtt(JSON.parse(connected.body));
        });
        stompClient.subscribe('/topic/auto-control', function (connected) {
            var msg = JSON.parse(connected.body);
            $("#mission-text").html(msg.msg);
            
            switch(msg.state) {
                case 'STOP': 
                    $("#cancel-btn").addClass("disabled-div");
                    $("#load-btn").removeClass("disabled-div");
                    $("#inputGroupSelect04").removeClass("disabled-div");
                break;
                case 'PAUSE': 
                    $("#pause-btn").addClass("disabled-div");
                    $("#inputGroupSelect04").addClass("disabled-div");
                    $("#load-btn").addClass("disabled-div");
                    $("#cancel-btn").removeClass("disabled-div");
                    $("#play-btn").removeClass("disabled-div");
                break;
                case 'WAITINGRES':
                    $("#inputGroupSelect04").addClass("disabled-div");
                    $("#load-btn").addClass("disabled-div");
                    $("#pause-btn").removeClass("disabled-div");
                    $("#cancel-btn").removeClass("disabled-div");
                    $("#play-btn").addClass("disabled-div");
                break;
                case 'RUN':
                    $("#inputGroupSelect04").addClass("disabled-div");
                    $("#load-btn").addClass("disabled-div");
                    $("#pause-btn").removeClass("disabled-div");
                    $("#cancel-btn").removeClass("disabled-div");
                    $("#play-btn").addClass("disabled-div");
                break;
            }
        });
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

function sendBoolean(msg, value) {
    stompClient.send("/app/cmd-bool", {}, JSON.stringify({'topic' : msg, 'robot' : robot, "value": value}));
}

function sendMqttConfig() {
    stompClient.send("/app/mqtt-config", {}, JSON.stringify({'ip' : $("#mqtt-ip").val(), 'id' : $("#mqtt-id").val(),
    'port' : $("#mqtt-port").val(), 'connected' : false}));
}

function sendMove(msg) {
    stompClient.send("/app/cmd-pos", {}, JSON.stringify({'direction' : msg, 'position' : parseInt($('#position-input').val(), 10), 
    'distances' : [], 'robot': robot}));
}

function sendCargo(type, direction) {
    stompClient.send("/app/cmd-cargo", {}, JSON.stringify({'type' : type, 'direction' :direction, 'robot': robot}));
}

function sendCmdAuto(cmd) {
    var scenario = $("#inputGroupSelect04").val();
    stompClient.send("/app/cmd-auto", {}, JSON.stringify({'file' : scenario, 'cmd' : cmd}));
}


$(function () {
    $("#stop-btn").click(function () { sendBoolean('/action/stop/cmd', true); });
    $("#mqtt-btn").click(function () { sendMqttConfig(); });
    $("#start-pos-btn").click(function () { sendBoolean('/action/start/cmd', true); });
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
       $("input:file").change(function (){
        $( "#form-file" ).submit();
      });
      $("#start-btn").click(function () { sendCmdAuto('PLAY'); });
      $("#pause-btn").click(function () { sendCmdAuto('PAUSE'); });
      $("#cancel-btn").click(function () { sendCmdAuto('STOP'); });

    connect();
});