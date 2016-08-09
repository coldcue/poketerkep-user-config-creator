// Requires
var casper = require('casper').create({
    verbose: false,
    logLevel: "debug"
});
var fs = require('fs');

var links = fs.read('links.txt').toString().split('\n');

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
		(function(ctr) {
	        casper.thenOpen(links[i], handleFinished.bind(casper, ctr));
	    })(i);
	}   
}

casper.run();
console.log('Users are registered!');
console.log('Work completed!');

function handleFinished(ctr) {
    this.echo('Finished ' + ctr + '.');
}