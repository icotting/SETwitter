var userservice = require('../services/userservice');

var User = function () {

	this.login = function (req, resp, params) {
		this.respond({params: params, newuser: params
			, loginname: params['loginname']
			, loginpassword: params['loginpassword']}
			, {
			format: 'html'
			, template: 'app/views/user/login'
		});
	};

	this.authenticate = function(req, resp, params) {
		var self = this;
		userservice.authenticate(params['loginname'], params['loginpassword'], function(err, user) {
			if (err) {
				params.errors = err;
		   		self.transfer('login');
			} else {
				self.session.set('userId', user.id);
		     	self.redirect('/');
			}
		});
	};

	this.logoff = function(req, resp, params) {
		this.session.set('userId', null);
		this.redirect('/');
	};

	this.newUser = function(req, resp, params) {
		var self = this;

		if ( params['confirmpassword'] != params['password']) {
			params.errors = {password: 'Passwords do not match'};
			self.transfer('login');
		} else {
			var user = geddy.model.User.create(params);
			userservice.createUser(user, function(err, user) {
				if (err) {
					params.errors = err;
		   			self.transfer('login');
				} else {
					self.session.set('userId', user.id);
		     		self.redirect('/');
				}
			});
		}
	};
}

exports.User = User;
