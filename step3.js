// Requirements
var guerrilla = require('node-guerrilla');
var fs = require('fs');
var util = require('util');
var request = require('request');


var users = fs.readFileSync('users.txt').toString().split('\n');
var emails = fs.readFileSync('emails.txt').toString().split('\n');
// Variables
var linksOutputFile = 'links.txt';

fs.stat(linksOutputFile, function(err, exist) {
    if (exist) {
        fs.unlink(linksOutputFile);
    }
});

function step3() {
    // Activate email address
    console.log('Creating activation txt...');
    var emailsActivated = 0;
    for (var i = 0; i < emails.length; i++) {

        var emailData = emails[i].split(' ');
        var userName = users[i];

        (function(userName, emailData) {
            if (emailData[1]) {
                guerrilla.get_link('Pok&eacute;mon_Trainer_Club_Activation', 'Verify your email', emailData[1]).then((link) => {
                    if (link) {
                        fs.appendFile(linksOutputFile, link + " " + userName + '\n', function(error) {
                            if (error) {
                                return console.log(error);
                            } else {
                                emailsActivated++;
                                console.log('[' + emailsActivated + '] - Links added for ' + userName);
                            }
                        });
                    }
                });
            }
        })(userName, emailData);

    }
}

console.log('Waiting 10 seconds...');
setTimeout(step3, 10000);
