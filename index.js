// Requires
var util = require('util');
var AWS = require('aws-sdk');
var gn = require('node-guerrilla');
var casper = require('casper').create({
    verbose: false,
    logLevel: "debug"
});

// Settings
var url = 'https://club.pokemon.com/us/pokemon-trainer-club/sign-up/';
var start = 100;
var end = 200;
var country = 'HU';
var dob = '1990-11-23';
var usernamePrefix = 'test31233'; //poketk

// AWS config
AWS.config.update({
    region: "eu-west-1"
});

// User array
var users = [];
var emails = [];

function runCasper() {
    casper.start();

    casper.on('complete.error', function(err) {
        this.echo("Complete callback has failed: " + err);
    });

    casper.on('error', function(msg, trace) {
        this.echo("Err: " + msg, "ERROR");
    });

    casper.on("page.error", function(msg, trace) {
        this.echo("Error: " + msg, "ERROR");
    });

    console.log('[o] Starting ' + start + ' to ' + (end - 1) + '.');

    for(var i = start; i < end; i++) {
        (function(ctr) {
            casper.thenOpen(url, handleDobPage.bind(casper, ctr)).then(handleSignupPage.bind(casper, ctr)).then(handleFinished.bind(casper, ctr));
        })(i);
    }

    casper.run();
}

// Upload users
function uploadUsers() {
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
            }
        });
    }
}

// Pages
function handleDobPage(ctr) {
    this.echo('[' + ctr + '] First Page: ' + this.getTitle());
    
    this.fill('form[name="verify-age"]', {
        'dob': dob,
        'country': country
    }, true);
}

function handleSignupPage(ctr) {
    // Server sometimes messes up and redirects us to the verify-age page again
    if(this.exists('form[name="verify-age"]')) {
        this.echo('[' + ctr + '] Server is acting up. Retrying...');
        handleDobPage.call(this, ctr);
        this.then(handleSignupPage.bind(casper, ctr));
        return;
    }

    var _this = this;
    
    // OK we're on the right page
    gn.get_email().then(account, function() {
        var _nick = usernamePrefix + ctr;
        var _pass = generatePassword(_nick);

        var formdata = {
            'terms': true
        };

        _this.echo('[' + ctr + '] Second Page: ' + _this.getTitle());

        // Use username & counter
        formdata['username'] = _nick;
        formdata['screen_name'] = _nick;
        formdata['email'] = account.email_addr;
        formdata['confirm_email'] = account.email_addr;
        formdata['password'] = _pass;
        formdata['confirm_password'] = _pass;

        // Save usernames
        users.push({
            userName: _nick,
            lastUsed: 0,
            banned: false
        });

        emails.push(account.sid_token);

        // Fill & submit
        _this.fill('form#user-signup-create-account-form', formdata, true);
    });
}

function handleFinished(ctr) {
    this.echo('Finished ' + ctr + '.');

    activateEmails();
}

function activateEmails() {
    gn.get_link_poll('Test Email', 'string_to_find', account.sid_token).then(function(link) {
        console.log(link);
    });
}

runCasper();