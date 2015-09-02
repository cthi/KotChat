var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var async = require('async');

var users = {};
var numUsers = 0;
var rooms = [{room : 'Sports', id : 0},
 {room : 'TV', id : 1},
 {room : 'World News', id : 2},
 {room : 'Programming', id : 3},
 {room : 'Android', id : 4},
 {room : 'Apple', id : 5}];

var roomJoinMsgs = ['Welcome to Sports', 'Welcome to TV', 'Welcome to World News', 'Welcome to Programming', 'Welcome to Android', 'Welcome to Apple']

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
			result : true,
			rooms : rooms
		})
	});

	socket.on('join room', function (msg){
		async.series([
			function(callback) {
				console.log(socket.roomNum);

				if (typeof socket.roomNum !== 'undefined') {
					socket.leave(socket.roomNum);
					
					io.to(socket.roomNum).emit('user left', {
  						message : socket.username
  					});

  					socket.roomNum = undefined;
				}

				callback();
			},
			function (callback) {
				socket.roomNum = msg.roomNum;
				socket.join(socket.roomNum);

				socket.emit('room join message', {
					message : roomJoinMsgs[socket.roomNum]
				});

				io.to(socket.roomNum).emit('user joined', {
					message : socket.username
				});

				callback()
			}
		]);
	});

	socket.on('send message', function(msg){
		if (socket.username) {
			socket.broadcast.to(socket.roomNum).emit('chat message', {
				message : msg.message,
				sender : socket.username
			});
		}
  	});

  	socket.on('disconnect', function(){
  		io.to(socket.roomNum).emit('user left', {
  			message : socket.username
  		});
  	});
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});
