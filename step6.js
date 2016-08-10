var util = require('util');
var AWS = require("aws-sdk");
var fs = require('fs');

AWS.config.update({
    region: "eu-west-1"
});

var users = fs.readFileSync('activated_users.txt').toString().split('\n');

var userConfigs = [];

console.log('Uploading users...');
for (var i = 0; i < users.length; i++) {
    if (users[i]) {
        userConfigs.push({
            userName: users[i].trim(),
            lastUsed: 0,
            banned: false
        });
    }
}

uploadUsers(userConfigs);

function uploadUsers(users) {
    var docClient = new AWS.DynamoDB.DocumentClient();

    for (var i = 0; i < users.length; i++) {
        var user = users[i];

        console.log(JSON.stringify(user));

        var params = {
            TableName: "UserConfig",
            Item: user
        }

        docClient.put(params, function(err, data) {
            if (err) {
                console.error("Unable to add item. Error JSON:", JSON.stringify(err, null, 2));
            } else {
                //console.log("Added point: " + JSON.stringify(data));
            }
        });
    }

}
