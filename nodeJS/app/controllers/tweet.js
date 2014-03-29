var userservice = require('../services/userservice');
var feedservice = require('../services/feedservice');

var Tweet = function () {

  this.refreshTweets = function (req, resp, params) {
    var self = this;
    userservice.loadUserFromSession(self.session, function(err, user) {
      user.getFeeds(function(err, feeds) {
        var selectedFeed = -1;
        feedservice.getTweetsToDisplay(feeds, selectedFeed, function(err, tweets) {
          if (err) {
            // do something
          } else {
            self.respond({params: params, feeds: feeds, tweets: tweets, selectedFeed: selectedFeed}, {
            format: 'html'
            , template: 'app/views/main/_tweetView'
            , layout: false
            });
          }
        });
      });
    });
  };

  this.addTweet = function(req, resp, params) {
    var self = this;
    feedservice.findFeedById(params['postFeed'], function(err, feed) {
      if (err) {
        console.log('ERROR getting the feed');
      } else {
        params['postdate'] = new Date();
        var tweet = geddy.model.Tweet.create(params);

        feedservice.postTweetToFeed(feed, tweet, function(err, tweet) {
          self.refreshTweets();
        });
      }
    });
  };
}

exports.Tweet = Tweet;
