var stompClientRobots = null;
var availableRobots = [];
var robot = null;
var mode = 'operate';
var role = 'admin';

function connectRobots() {
    var socket = new SockJS('/client-websocket');
    stompClientRobots = Stomp.over(socket);
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

window.onbeforeunload = function () {
    stompClientRobots.onclose = function () { }; // disable onclose handler first
    stompClientRobots.close();
};


$(function () {
    var modeLocal = localStorage.getItem('mode');
    if (modeLocal !== undefined) {
        if (modeLocal == 'operate') {
            $("#operating-btn").removeClass("btn-secondary").addClass("btn-success");
            $("#setting-btn").removeClass("btn-success").addClass("btn-secondary");
        } else if (modeLocal == 'setting') {
            $("#operating-btn").removeClass("btn-success").addClass("btn-secondary");
            $("#setting-btn").removeClass("btn-secondary").addClass("btn-success");
        }
    }

    $("#operating-btn").click(function () {
        //TODO check role

        if (role == 'admin') {
            $("#operating-btn").removeClass("btn-secondary").addClass("btn-success");
            $("#setting-btn").removeClass("btn-success").addClass("btn-secondary");
            mode = 'operate';
            localStorage.setItem('mode', mode);
        }
    });
    $("#setting-btn").click(function () {
        if (role == 'admin') {
            $("#operating-btn").removeClass("btn-success").addClass("btn-secondary");
            $("#setting-btn").removeClass("btn-secondary").addClass("btn-success");
            mode = 'setting';
            localStorage.setItem('mode', mode);
        }
    });
    $('select').on('change', function () {
        localStorage.setItem('robot', this.value);
        newRobot(this.value);
    });

    connectRobots();
});