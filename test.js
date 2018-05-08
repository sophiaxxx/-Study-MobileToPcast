

var dgram = require('dgram');
var s = dgram.createSocket('udp4');

var MULTICAST_IP = "239.5.6.7"
//var LOCAL_IP     = "192.168.0.4"

s.bind(12332, MULTICAST_IP, function() {
  s.addMembership(MULTICAST_IP);
  console.log("listening on a specific address");
});

s.on("message", function (msg, rinfo) {
  console.log("server got: " + msg + " from " +
  rinfo.address + ":" + rinfo.port);
});