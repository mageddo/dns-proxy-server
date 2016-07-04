module.exports = function(app){

	app.get('/containers', (req, res) => {
		var util = require('util');
		app.data.containerEntries.forEach(c => {
			res.write(util.format('container=%s, ip=%s, domain=%s', c.container, c.ip, c.domain));
			res.write('\n');
		});
		res.end();
	});
}
