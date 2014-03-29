var Tweet = function () { 
	this.property('content', 'string', {required: true});
	this.property('postdate', 'datetime', {required: true});

	this.belongsTo('Feed');

}

exports.Tweet = Tweet;