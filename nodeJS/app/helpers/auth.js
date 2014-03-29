exports.requireAuth = function () {

  if (!(this.session.get('userId') ||
      this.name == 'User')) {
    // Record the page the user was trying to get to, will
    // try to return them there after login
    this.session.set('successRedirect', this.request.url);
    this.redirect('/user/login');
  }

};
