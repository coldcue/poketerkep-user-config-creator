var util = require('util');
var AWS = require("aws-sdk");

AWS.config.update({
    region: "eu-west-1"
});


var users = [];

for (var i = 5; i < 48; i++) {
    users.push({
        userName: "poketk0" + ((i < 10) ? "0" : "") + i,
        lastUsed: 0,
        banned: false
    });
}

console.log(JSON.stringify(users));

uploadUsers(users);

function uploadUsers(users) {
    var docClient = new AWS.DynamoDB.DocumentClient();

    for (var i = 0; i < users.length; i++) {
        var user = users[i];

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
