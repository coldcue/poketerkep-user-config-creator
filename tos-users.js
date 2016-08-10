var AWS = require('aws-sdk');
var md5 = require('md5');
var fs = require('fs');

var fileName = 'accounts.txt';

// AWS config
AWS.config.update({
    region: 'eu-west-1'
});

getUsers();

function getUsers() {
	console.log('Getting users...');

	var docClient = new AWS.DynamoDB.DocumentClient();

	var params = {
        TableName: 'UserConfig'
    }

	docClient.scan(params, function(err, data) {
        if (err) {
        	console.log(err);
        } else {
        	convertUsers(data.Items);
        }
    });
}

function convertUsers(users) {
	var output = '';

	for(var i = 0; i < users.length; i++) {
		output += users[i].userName + ':' + md5(users[i].userName).substr(0, 8) + '\n';
	}

	console.log(users.length + ' users readed from database!');

	saveOutput(output);
}

function saveOutput(output) {
	fs.writeFile(fileName, output.trim(), function(error) {
        if(error) {
            return console.log(error);
        } else {
            console.log('Output generated!');
        }
    });
}