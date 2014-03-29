
var UserService = function() {
	this.createUser = function(userModel, action) {

		geddy.model.User.first({username: userModel.username}, function(err, namematch) {
			if (namematch) {
				action({username: 'A user already exists with that name'}, null);
			} else {
				geddy.model.User.first({email: userModel.email}, function(err, emailmatch) {
					if (emailmatch) {
						action({email: 'A user already exists with that email address'}, null);
					} else {
						userModel.save(function(err, data) {
										if (err) {
											action(err);
										} else {
											action(null, data);
										}
							});
					}
				});
			}
		});
	};

	this.authenticate = function(username, password, action) {
		geddy.model.User.first({username: username, password: password}, function(err, user) {
			if (err) {
				action({loginname: 'A system error occurred during login'});
			} else if (!user) {
				action({loginname: 'Invalid id or password'});
			} else {
				action(null, user);
			}
		});
	}

	this.loadUserFromSession = function(session, action) {
		geddy.model.User.first(session.get('userId'), function(err, data) {
			action(null, data);
		});
	};
}

module.exports = new UserService();
