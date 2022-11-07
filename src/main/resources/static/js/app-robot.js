var stompClient = null;
var lastParams = [];
function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.debug = null
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/params', function (msg) {
            var message = JSON.parse(msg.body);
            lastParams = message;
            $(".finger").removeClass("finger-success");
            message.forEach(element => {
                if(element.paramName.startsWith("info/sensor/")) {
                    let paramName = element.paramName;
                    paramName =  paramName.replace("info/sensor/", "");
                    if(element.value) {
                        $("#" + paramName).addClass("finger-success");
                    } else {
                        $("#" + paramName).removeClass("finger-success");
                    }
                }
            });
        });
        setInterval(() => {
            if(robot !== null) {
                stompClient.send("/app/params", {}, robot.topicId);
            }
          }, "1000")
    });
}

function sendCmdMotor(topic, direction, speed) {
    stompClient.send("/app/cmd-motor", {}, JSON.stringify({'topic' : topic, 'direction' : direction, 'robot': robot,
        'speed' : speed }));
}

function sendParam(topic, value) {
    stompClient.send("/app/cmd-param", {}, JSON.stringify({'topic' : topic, 'value' : value, 'robot': robot }));
}


$(function () {
    connect();
    $("#platform-range").change(function(){
        $("#platform-range-label").html($(this).val());

    });
    $("#load-handling-range").change(function(){
        $("#load-handling-range-label").html($(this).val());

    });
    $("#rear-right-range").change(function(){
        $("#rear-right-range-label").html($(this).val());

    });
    $("#front-right-range").change(function(){
        $("#front-right-range-label").html($(this).val());
    });
    $("#rear-left-range").change(function(){
        $("#rear-left-range-label").html($(this).val());
    });
    $("#front-left-range").change(function(){
        $("#front-left-range-label").html($(this).val());
    });
    // platform 
    $("#motor-up-nav-sensor-btn").click(function () {
        var speed = $("#platform-range").val();
        sendCmdMotor("move", false, speed);
    });
    $("#motor-down-nav-sensor-btn").click(function () {
        var speed = $("#platform-range").val();
        sendCmdMotor("move", true, speed);
    });
    // load handling
    $("#motor-left-handling-btn").click(function () {
        var speed = $("#load-handling-range").val();
        sendCmdMotor("lhd", true, speed);
    });
    $("#motor-right-handling-btn").click(function () {
        var speed = $("#load-handling-range").val();
        sendCmdMotor("lhd", false, speed);
    });
    // rear right finger
    $("#motor-up-rear-right-finger-btn").click(function () {
        var speed = $("#rear-right-range").val();
        sendCmdMotor("fng_r_r", false, speed);
    });
    $("#motor-down-rear-right-finger-btn").click(function () {
        var speed = $("#rear-right-range").val();
        sendCmdMotor("fng_r_r", true, speed);
    });
    // front right
    $("#motor-up-front-right-finger-btn").click(function () {
        var speed = $("#front-right-range").val();
        sendCmdMotor("fng_f_r", false, speed);
    });
    $("#motor-down-front-right-finger-btn").click(function () {
        var speed = $("#front-right-range").val();
        sendCmdMotor("fng_f_r", true, speed);
    });
    // rear left
    $("#motor-up-rear-left-finger-btn").click(function () {
        var speed = $("#rear-left-range").val();
        sendCmdMotor("fng_r_l", false, speed);
    });
    $("#motor-down-rear-left-finger-btn").click(function () {
        var speed = $("#rear-left-range").val();
        sendCmdMotor("fng_r_l", true, speed);
    });
    // front left
    $("#front-up-left-finger-btn").click(function () {
        var speed = $("#front-left-range").val();
        sendCmdMotor("fng_f_l", false, speed);
    });
    $("#front-down-left-finger-btn").click(function () {
        var speed = $("#front-left-range").val();
        sendCmdMotor("fng_f_l", true, speed);
    });
    $("#send-params-btn").click(function () {
        var maxSpeed = $("#maxSpeed").val();
        sendParam('move_vel', maxSpeed);
        var lhdTime = $("#lhdTime").val();
        sendParam('lhd_time', lhdTime);
        var fingerSpeed = $("#fingerSpeed").val();
        sendParam('fng_vel', fingerSpeed);
        var lhdExtension = $("#lhdExtension").val();
        sendParam('lhd_ext', lhdExtension);
        
    });
    $("#read-params-btn").click(function () {
        console.log(lastParams);
        lastParams.forEach(element => {
            if (element.paramName === "move_vel") {
                $("#maxSpeed").val(element.value);
            } else if (element.paramName === "lhd_time") {
                $("#lhdTime").val(element.value);
            } else if (element.paramName === "fng_vel") {
                $("#fingerSpeed").val(element.value);
            } else if (element.paramName === "lhd_ext") {
                $("#lhdExtension").val(element.value);
            }
        });
    });

});