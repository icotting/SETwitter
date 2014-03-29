var User = function () {
	this.property('name', 'string', {required: true});
	this.property('location', 'string', {required: true});
	this.property('email', 'string', {required: true});
	this.property('username', 'string', {required: true});
	this.property('password', 'string', {required: true});

	this.hasMany('Feeds');
	this.hasMany('Subscribers');
	this.hasMany('Subscriptions', {model: 'Feeds', through: "Subscribers"});
};

exports.User = User;
