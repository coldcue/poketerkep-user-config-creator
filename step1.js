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
console.log('Generating email addresses...');
for(var i = 0; i < config.count; i++) {
	guerrilla.get_email().then(function(account) {
		var emailAddr = account.email_addr.split('@');
		guerrilla.set_email(account.sid_token, emailAddr[0], 'sharklasers.com');

		fs.appendFile(emailsOutputFile, emailAddr[0] + '@' + 'sharklasers.com' + ' ' + account.sid_token + '\n',  function(error) {
		    if(error) {
		        return console.log(error);
		    }
		});
	});
}
console.log('Email addresses are ready!');
