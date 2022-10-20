var stompClient = null;

function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.debug = null
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/params', function (msg) {
            var message = JSON.parse(msg.body);
        //    console.log(message);
            message.forEach(element => {
          //      console.log(element);
                if(element.value) {
                    $("#" + element.paramName).addClass("finger-success");
                } else {
                    $("#" + element.paramName).removeClass("finger-success");
                }
            });
        });
        setInterval(() => {
            //console.log("timeout");
            if(robot !== null) {
                stompClient.send("/app/params", {}, robot.topicId);
            }
          }, "1000")
          
        //stompClient.send("/app/diagnostics", {}, JSON.stringify());
    });
}

function sendCmdMotor(topic, direction, speed) {
    console.log({'topic' : topic, 'direction' : direction, 'robot': robot,
    'speed' : speed });
    stompClient.send("/app/cmd-motor", {}, JSON.stringify({'topic' : topic, 'direction' : direction, 'robot': robot,
        'speed' : speed }));
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
});