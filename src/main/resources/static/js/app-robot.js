var stompClient = null;

function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/params', function (msg) {
            var message = JSON.parse(msg.body);
            console.log(message);
            message.forEach(element => {
                console.log(element);
            });
        });
        setInterval(() => {
            console.log("timeout");
            if(robot !== null) {
                stompClient.send("/app/params", {}, robot.topicId);
            }
          }, "1000")
          
        //stompClient.send("/app/diagnostics", {}, JSON.stringify());
    });
}


$(function () {
    connect();
});