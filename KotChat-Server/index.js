var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var users = {};
var numUsers = 0;
var rooms = ['Sports', 'TV', 'World News', 'Programming', 'Android', 'Apple'];
var roomJoinMsgs = ['Welcome to Sports', 'Welcome to TV', 'Welcome to Programming', 'Welcome to Android', 'Welcome to Apple']

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){
	socket.emit('connection message', {
		message : "Welcome. Please enter a nickname."
	});

	socket.on('set username', function(msg){
		socket.username = msg.username;
		
		++numUsers;
		users[msg.username] = msg.username

		socket.emit('username set', {
			result : true
		})
	});

	socket.on('join room', function (msg){
		socket.roomNum = msg.roomNum;
		socket.join(rooms[socket.roomNum]);

		socket.emit('room join message', {
			message : roomJoinMsgs[socket.roomNum]
		});

		io.to(rooms[socket.roomNum]).emit('user joined', {
			message : socket.username
		});
	});

	socket.on('send message', function(msg){
		if (socket.username) {
			socket.broadcast.to(rooms[socket.roomNum]).emit('chat message', {
				message : msg.message,
				sender : socket.username
			});
		}
  	});

	socket.on('leave room', function(){
		var roomNum = socket.roomNum;
		socket.leave(rooms[socket.roomNum])
		socket.roomNum = undefined;

  		io.to(rooms[roomNum]).emit('user left', {
  			message : socket.username
  		});
	});

  	socket.on('disconnect', function(){
  		io.to(rooms[socket.roomNum]).emit('user left', {
  			message : socket.username
  		});
  	});
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});
