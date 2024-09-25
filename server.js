var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mysql = require('mysql2');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
app.get('/', function (req, res) {
    return res.send({ error: true, message: 'hello HieuPV' })
});

var dbConn = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS
});

 console.log("Conneting to MYSQL IP= " + process.env.DB_HOST + " USER=" +  process.env.DB_USER + " PASS=" + process.env.DB_PASS);

dbConn.connect(function(err) {
  if (err) 
   {
     console.log("Connet to MYSQL ERROR: "+ err);
     throw err;
   }
  console.log("MYSQL Connected!");

  dbConn.query("CREATE DATABASE IF NOT EXISTS userapidb", function (err, result) {
      if (err) 
      {
        console.log("Continue running, but Error when create DB: "+ err);
      }    
     console.log(" DB userapidb OK");
      });

      dbConn.query("USE userapidb", function (err, result) {
        if (err) 
        {
          console.log("Continue running, but Error when create DB: "+ err);
        }    
       console.log(" Select userapidb OK");
        });

      var sql = " CREATE TABLE IF NOT EXISTS userapidb.users ( id int(11) NOT NULL, name varchar(200) NOT NULL, email varchar(200) NOT NULL, created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ) ENGINE=InnoDB ";
      
      dbConn.query(sql, function (err, result) {
        if (err) {
                    console.log("Co loi khi tao table " + err);
                  }
         console.log("DB Ready. App is running... ");
        });
     var sql1="INSERT INTO userapidb.users (id, name, email, created_at) VALUES (1, 'Max', 'max@gmail.com', '2020-03-18 23:20:20'), (2, 'John', 'john@gmail.com', '2020-03-18 23:45:20'), (3, 'David', 'david@gmail.com', '2020-03-18 23:30:20'), (4, 'James', 'james@gmail.com', '2020-03-18 23:10:20'), (5, 'Shaw', 'shaw@gmail.com', '2020-03-18 23:15:20') "; 
   //  sql1="select * from users"; 
     
     dbConn.query(sql1, function (err, result) {
     if (err) {
                 console.log("Co loi khi insert data " + err);
               }
     });
   });

//Conn.end();

 




app.get('/users', function (req, res) {
    dbConn.query('SELECT * FROM users', function (error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'users list.' });
    });
});
app.get('/user/:id', function (req, res) {
    let user_id = req.params.id;
    if (!user_id) {
        return res.status(400).send({ error: true, message: 'Please provide user_id' });
    }
    dbConn.query('SELECT * FROM users where id=?', user_id, function (error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results[0], message: 'users list.' });
    });
});
app.post('/user', function (req, res) {
    let user = req.body.user;
    if (!user) {
        return res.status(400).send({ error:true, message: 'Please provide user' });
    }
    dbConn.query("INSERT INTO users SET ? ", { user: user }, function (error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'New user has been created successfully.' });
    });
});
app.put('/user', function (req, res) {
    let user_id = req.body.user_id;
    let user = req.body.user;
    if (!user_id || !user) {
        return res.status(400).send({ error: user, message: 'Please provide user and user_id' });
    }
    dbConn.query("UPDATE users SET user = ? WHERE id = ?", [user, user_id], function (error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'user has been updated successfully.' });
    });
});
app.delete('/user', function (req, res) {
    let user_id = req.body.user_id;
    if (!user_id) {
        return res.status(400).send({ error: true, message: 'Please provide user_id' });
    }
    dbConn.query('DELETE FROM users WHERE id = ?', [user_id], function (error, results, fields) {
        if (error) throw error;
        return res.send({ error: false, data: results, message: 'User has been updated successfully.' });
    });
});

app.listen(3000, function () {
    console.log('Node app is running on port 3000');
});

module.exports = app;
