var express = require('express');
var http = require('http');
var bodyParser = require('body-parser');
var fs = require('fs');

var app = express();
app.use(bodyParser.urlencoded({extended:false}));
var server = http.createServer(app);
server.listen(8080, function(err) {
   if ( err ) {
      console.error('Error', err);
   }
   else {
      console.log('Http server is listening @ 8080');
   }
});

app.get('/login', showLoginForm);
app.post('/login', login);

function showLoginForm(req, res) {
   var html = '<html><body>';
   html += '<form method="POST" action="/login">';
   html += '<input type="text" name="userid"><br />';
   html += '<input type="password" name="password"><br />';
   html += '<input type="submit" value="LogIn">';
   html += '</form>';
   html += '</body></html>';
   res.send(html);
}

function login(req, res) {
   var userid = req.body.userid;
   var password = req.body.password;
   console.log('login == id : ' + userid + ' pw : ' + password);
   if ( userid == 'user' && password == '1234' )
      res.send('Welcome');
   else
      res.sendStatus(401);
}

var options = {
    key: fs.readFileSync('key.pem'),
    cert: fs.readFileSync('cert.pem')
};

var https = require('https');
var secureServer = https.createServer(options, app);
secureServer.listen(8081, function(err) {
   if ( err ) {
      console.error('Error', err);
   }
   else {
      console.log('Https server is listening @ 8080');
   }   
});


