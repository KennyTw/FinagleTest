var http = require('http'),   
	express = require('express');

	var app = express()	 
	  , server = http.createServer(app);
	  

   app.get('/', function(req, res){
		var date = new Date();
		console.log(date);
		res.send('hello world');
   });
   
    app.get('/ok', function(req, res){
		var date = new Date();
		console.log(date);
		res.send('hello world ok');
   });
	
var args = process.argv.slice(2);	
var port = 3000;	
if (args.length > 0)
		port = args[0];		

server.listen(port);
//app.listen(80); //the port you want to use