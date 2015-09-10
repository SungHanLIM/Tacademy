var net = require('net');

var sockets = [];

var server = net.createServer(function (socket) {
   // Connection Event
   console.log('Remote Address : ', socket.remoteAddress);
   
   // 클라이언트와 접속한 소켓을 채팅 클라이언트 목록에 추가
   sockets.push(socket);      
   socket.write('Welcome to TCP Chat Service\n');

   socket.on('data', function (data) {
      console.log(socket.remoteAddress + ' << ' + data);      
      sockets.forEach(function (item) {
         item.write(socket.remoteAddress + ' >> ' + data + '\n');
      });
   });

   socket.on('end', function () {
      console.log('connection end')
      // 소켓 목록에서 삭제
      var index = sockets.indexOf(socket);
      sockets.splice(index, 1);
   });
});

server.listen(3000);

// 서버 IP 주소 얻기
var serverIp = require('./serverIp');

console.log('Tch Chat server is running on ' + serverIp.getIPAddress() + ':3000');