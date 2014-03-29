var userservice = require('../services/userservice');
var feedservice = require('../services/feedservice');

var Feed = function () {

  this.addFeed = function(req, resp, params) {

    var self = this;
    userservice.loadUserFromSession(self.session, function(err, user) {
      var feed = geddy.model.Feed.create(params);
      feedservice.addFeed(user, feed, function(err, feeds) {
        self.respond({params: params, feeds: feeds, selectedFeed: -1}, {
          format: 'html'
          , template: 'app/views/main/_feedView'
          , layout: false
        });
      });
    });
  };
}

exports.Feed = Feed;
