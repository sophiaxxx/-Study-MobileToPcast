
var s_port = 12332;
var multicastAddr = '239.5.6.7'; 
var dgram = require("dgram");
var server = dgram.createSocket("udp4");
var os = require('os');
var ifaces = os.networkInterfaces();
var hostIP = '';
var LOCAL_INTERFACES = [];
var hostIP = '';

/**
 * get local interfaces ip
 */
Object.keys(ifaces).forEach(function (ifname) {
  var alias = 0;

  ifaces[ifname].forEach(function (iface) {
    if ('IPv4' !== iface.family || iface.internal !== false) {
      // skip over internal (i.e. 127.0.0.1) and non-ipv4 addresses
      return;
    }
    if (alias >= 1) {
      // this single interface has multiple ipv4 addresses
      console.log(ifname + ':' + alias, iface.address);
    } else {
      // this interface has only one ipv4 adress
      console.log(ifname, iface.address);
    }
    hostIP = iface.address;
  });
});



// 建立 UDP 用戶端
console.info('Now create UDP Client...');

// Listen 状態になったときの処理
server.on("listening", function() {
  var address = server.address();
  console.log("server listening " + multicastAddr + ":" + address.port);
  console.log(`UDP socket listening on port ${s_port}, ${hostIP} interface, join ${multicastAddr} group`);
});

// メッセージを受信した時の処理
server.on("message", function(msg, rinfo) {
  console.log("server got a message from " + rinfo.address + ":" + rinfo.port);
  console.log("  HEX  : " + msg.toString('hex'));
  console.log("  ASCII: " + msg);
  var ack = new Buffer("ack");
  server.send(ack, 0, ack.length, rinfo.port, rinfo.address, function(err, bytes) {
    console.log("sent ACK.");
  }); 
});

// メッセージ受信でエラーがあったときの処理
server.on("error", function(err) {
  console.log("server error: \n" + err.stack);
  server.close();
});

// Socket がクローズしたときの処理
server.on("close", function() {
  console.log("closed.");
});

// 待受開始
server.bind(s_port);