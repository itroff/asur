var stompClientRobots = null;
var availableRobots = [];
var robot = null;
var mode = 'operate';
var role = 'user';

function connectRobots() {
    var socket = new SockJS('/client-websocket');
    stompClientRobots = Stomp.over(socket);
    stompClientRobots.debug = null
    stompClientRobots.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClientRobots.subscribe('/topic/connected', function (connected) {
            showConnected(JSON.parse(connected.body));
        });
        sendRequestConnected();
    });
}

function sendRequestConnected() {
    stompClientRobots.send("/app/robots", {}, JSON.stringify());
}

function sendMode(mode) {
    stompClientRobots.send("/app/mode", {}, JSON.stringify());
}

function newRobot(name) {
    console.log(name);
    availableRobots.forEach(element => {
        if (element.topicId === name) {
            robot = element;
            return;
        }
    });
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
        case 'FAULT':
            $("#robot-status").removeClass("dot-grey dot-red dot-green").addClass("dot-red");
            break;
    }
}

function sendBoolean2(msg, value) {
    if(role==='admin') {
        stompClient.send("/app/cmd-bool", {}, JSON.stringify({'topic' : msg, 'robot' : robot, "value": value}));
    }
}

$(function () {
    var modeLocal = localStorage.getItem('mode');
    role = $("#role-id").val();
    if (role === 'user') {
        modeLocal = 'operate';
    }
    console.log(modeLocal);
    if (modeLocal === 'operate' || modeLocal === 'setting') {
        if (modeLocal == 'operate') {
            $("#operating-btn").removeClass("btn-secondary").addClass("btn-success");
            $("#setting-btn").removeClass("btn-success").addClass("btn-secondary");
            $("#main-robot").addClass("disabled-div");
            $("#card-last").addClass("disabled-div");
        } else if (modeLocal == 'setting') {
            $("#operating-btn").removeClass("btn-success").addClass("btn-secondary");
            $("#setting-btn").removeClass("btn-secondary").addClass("btn-success");
            $("#main-robot").removeClass("disabled-div");
            $("#card-last").removeClass("disabled-div");
        }
    } else {
        $("#operating-btn").removeClass("btn-secondary").addClass("btn-success");
        $("#setting-btn").removeClass("btn-success").addClass("btn-secondary");
        $("#main-robot").addClass("disabled-div");
        $("#card-last").addClass("disabled-div");
    }

    $("#operating-btn").click(function () {
        if (role == 'admin') {
            $("#operating-btn").removeClass("btn-secondary").addClass("btn-success");
            $("#setting-btn").removeClass("btn-success").addClass("btn-secondary");
            $("#main-robot").addClass("disabled-div");
            $("#card-last").addClass("disabled-div");
            mode = 'operate';
            localStorage.setItem('mode', mode);
            sendBoolean2("/info/mode/cmd", false);
        }
    });
    $("#setting-btn").click(function () {
        if (role == 'admin') {
            $("#operating-btn").removeClass("btn-success").addClass("btn-secondary");
            $("#setting-btn").removeClass("btn-secondary").addClass("btn-success");
            $("#main-robot").removeClass("disabled-div");
            $("#card-last").removeClass("disabled-div");
            mode = 'setting';
            localStorage.setItem('mode', mode);
            sendBoolean2("/info/mode/cmd", true);
        }
    });
    $('select').on('change', function () {
        localStorage.setItem('robot', this.value);
        newRobot(this.value);
    });

    connectRobots();
});