// Requirements
var guerrilla = require('node-guerrilla');
var fs = require('fs');
var config = require('./config.json');

// Variables
var emailsOutputFile = 'emails.txt';

fs.stat(emailsOutputFile, function(err, exist) {
	if(exist) {
		fs.unlink(emailsOutputFile);
	}
});

// Generate email address
for(var i = 0; i < config.count; i++) {
	guerrilla.get_email().then(function(account) {
		fs.appendFile('emails.txt', account.email_addr + ' ' + account.sid_token + '\n',  function(error) {
		    if(error) {
		        return console.log(error);
		    }
		});
	});
}
