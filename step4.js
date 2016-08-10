// Requires
var casper = require('casper').create({
    verbose: false,
    logLevel: "debug"
});
var fs = require('fs');

var links = fs.read('links.txt').toString().split('\n');

var usersOutputFile = 'activated_users.txt';

if (fs.exists(usersOutputFile)) {
    fs.remove(usersOutputFile);
}

console.log('Activating links...');

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

console.log('[o] Starting ' + 0 + ' to ' + (links.length - 1) + '.');

for(var i = 0; i < links.length; i++) {
	if(links[i]) {
    var data = links[i].split(' ');
    var link = data[0];
    var user = data[1];

		(function(ctr) {
	        casper.thenOpen(link, handleFinished.bind(casper, ctr));
	    })(user);
	}
}

casper.run();
console.log('Users are registered!');
console.log('Work completed!');

function handleFinished(ctr) {
    // Log it in the file of used nicknames
    fs.write(usersOutputFile, ctr + '\n', 'a');
    this.echo('Finished ' + ctr + '.');
}
