// Requirements
var guerrilla = require('node-guerrilla');
var fs = require('fs');
var util = require('util');
var AWS = require("aws-sdk");

AWS.config.update({
    region: "eu-west-1"
});


var users = fs.readFileSync('users.txt').toString().split('\n');
var emails = fs.readFileSync('emails.txt').toString().split('\n');

// Activate email address
for (var i = 0; i < emails.length; i++) {
    var emailData = emails[i].split(' ');

    if (emailData[1]) {
        guerrilla.get_link('Pokémon Trainer Club Activation', 'string_to_find', emailData[1]).then(function(link) {
            console.log(link);
        });
    }
}

var userConfigs = [];

for (var i = 0; i < users.length; i++) {
    userConfigs.push({
        userName: users[i],
        lastUsed: 0,
        banned: false
    });
}

uploadUsers(userConfigs);

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
