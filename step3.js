// Requirements
var guerrilla = require('node-guerrilla');
var fs = require('fs');

//var users = fs.readFileSync('users.txt').toString().split('\n');
var emails = fs.readFileSync('emails.txt').toString().split('\n');

// Activate email address
for(var i = 0; i < emails.length; i++) {
	var emailData = emails[i].split(' ');

	guerrilla.get_link('Asd', 'Pokémon Trainer Club Activation', emailData.sid_token).then(function(link) {
		console.log(link);
	});
}