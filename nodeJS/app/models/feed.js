var Feed = function () {
	this.property('name', 'string', {required: true});

	this.belongsTo('User');
	this.hasMany('Tweets');
	this.hasMany('Subscribers');

	this.hasMany('Users', {through: "Subscribers"});
}

exports.Feed = Feed;
