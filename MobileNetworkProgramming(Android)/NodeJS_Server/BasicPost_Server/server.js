var express = require('express');
var bodyParser = require('body-parser');

var app = express();
app.use(bodyParser.urlencoded({ extended: false }))

app.get('/', showMovieList);
app.get('/post', showNewMovieForm);
app.post('/', handlePostRequest);

app.listen(3000, function() {
   console.log('Server is listening @ 3000');
});

var movieList = [{title:'인터스텔라', director : '크리스토퍼 놀란'}];


function handlePostRequest(req, res) {
   var title = req.body.title;
   var director = req.body.director;
   if ( !title || !director ) {
      res.sendStatus(400);
   }   
   else {
      console.log('New Post == title : ' + title + ' director : ' + director);
      movieList.push({title:title, director:director});
      res.redirect('/');
   }
}

function showNewMovieForm(req, res) {
	res.writeHeader(200, {'Content-Type':'text/html; charset=utf-8'});
   
   var body = '<html><body>'
   body += '<h3>새 영화 입력</h3>';   
 	body += '<form method="post" action=".">';
   body += '<div><label>영화 제목</label><input type="text" placeholder="영화제목" name="title"></div>';
   body += '<div><label>감독</label><input type="text" name="director" placeholder="감독"></div>';
   body += '<div><input type="submit" value="upload"></div>';
   body += '</form>';
   body += '</body></html>';
   res.end(body);
  
}

function showMovieList(req, res) {   
	res.writeHeader(200, {'Content-Type':'text/html; charset=utf-8'});
   
   var body = '<html><body>'
   body += '<h3>Favorite Movie</h3>';
	body += '<div><ul>';
	
	movieList.forEach(function(item) {
		body += '<li>' + item.title + '(' + item.director + ')</li>';
	}, this);
	body += '</ul></div>';
   
   body += '<div><a href="post">Post New Movie</a></div>';
	
   body += '</body></html>';
   res.end(body);
}