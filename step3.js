// Requirements
var guerrilla = require('node-guerrilla');
var fs = require('fs');
var util = require('util');
var AWS = require("aws-sdk");
var request = require('request');

AWS.config.update({
    region: "eu-west-1"
});

var users = fs.readFileSync('users.txt').toString().split('\n');
var emails = fs.readFileSync('emails.txt').toString().split('\n');

// Variables
var linksOutputFile = 'links.txt';

fs.stat(linksOutputFile, function(err, exist) {
    if(exist) {
        fs.unlink(linksOutputFile);
    }
});

function step3() {
    // Activate email address
    console.log('Creating activation txt...');
    var emailsActivated = 0;
    for(var i = 0; i < emails.length; i++) {
        var emailData = emails[i].split(' ');

        if(emailData[1]) {
            guerrilla.get_link('Pok&eacute;mon_Trainer_Club_Activation', 'Verify your email', emailData[1]).then(function(link) {
                if(link) {
                    fs.appendFile(linksOutputFile, link + '\n',  function(error) {
                            if(error) {
                                return console.log(error);
                            } else {
                                emailsActivated++;
                                console.log('Links added:' + emailsActivated);
                            }
                        });
                }
            });
        }
    }

    var userConfigs = [];

    console.log('Uploading users...');
    for (var i = 0; i < users.length; i++) {
        if(users[i]) {
            userConfigs.push({
                userName: users[i].trim(),
                lastUsed: 0,
                banned: false
            });
        }
    }

    uploadUsers(userConfigs);
}

console.log('Waiting 10 seconds...');
setTimeout(step3, 10000);

function uploadUsers(users) {
    var docClient = new AWS.DynamoDB.DocumentClient();

    for (var i = 0; i < users.length; i++) {
        var user = users[i];

        var params = {
            TableName: "UserConfig",
            Item: user
        }

        console.log("Adding user: " + JSON.stringify(user));

        docClient.put(params, function(err, data) {
            if (err) {
                console.error("Unable to add item. Error JSON:", JSON.stringify(err, null, 2));
            } else {
                //console.log("Added point: " + JSON.stringify(data));
            }
        });
    }

}
