var util = require('util');
var fs = require('fs');
var md5 = require('md5');

console.log("Creating accounts.txt to accept ToS");

var users = fs.readFileSync('activated_users.txt').toString().split('\n');

var accountsFile = 'accounts.txt'

fs.stat(accountsFile, function(err, exist) {
    if (exist) {
        fs.unlink(accountsFile);
    }
});


users.forEach(function(user) {
    if (user) {
        fs.appendFile(accountsFile, user + ':' + md5(user).substr(0, 8) + '\n', function(error) {
            if (error) {
                return console.log(error);
            }
        });
    }
});
