// Requirements
var guerrilla = require('node-guerrilla');
var fs = require('fs');

//var users = fs.readFileSync('users.txt').toString().split('\n');
var emails = fs.readFileSync('emails.txt').toString().split('\n');

// Activate email address
for(var i = 0; i < emails.length; i++) {
	var emailData = emails[i].split(' ');

	if(emailData[1]) {
		guerrilla.get_link('Pokémon Trainer Club Activation', 'string_to_find', emailData[1]).then(function(link) {
			console.log(link);
		});
	}
}