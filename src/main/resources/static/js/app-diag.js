var stompClient = null;

function connect() {
    var socket = new SockJS('/client-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/diagnostics', function (msg) {
            var message = JSON.parse(msg.body);
            message.forEach(element => {
            $("#card-insert").append('<p><b>' + element.dt + ':</b> ' + element.src + ' -> ' + element.dst + ' <b>' + element.msg  +'</b></p>');
            });
        });
        stompClient.send("/app/diagnostics", {}, JSON.stringify());
    });
}


$(function () {
    connect();
});