var FeedService = function() {
  var userservice = require('../services/userservice');

  this.addFeed = function(userModel, feedModel, action) {
    var self = this;
    userModel.addFeed(feedModel);
    userModel.save(function(err, data) {
      if (err) {
        action(err, null);
      } else {
        data.getFeeds(function(err, data) {
          action(null, data);
        });
      }
    });
  };

  this.postTweetToFeed = function(feedModel, tweetModel, action) {
    var self = this;
    feedModel.addTweet(tweetModel);
    feedModel.save(function(err, data) {
      if (err) {
        action(err, null);
      } else {
        action(null, data);
      }
    });
  };

  this.findFeedById = function(feedId, action) {
    geddy.model.Feed.first({id: feedId}, function(err, feed) {
      if (err || !feed) {
        action({feed: 'The feed was not found'}, null);
      } else {
        action(null, feed);
      }
    });
  };

  this.getTweetsToDisplay = function(feeds, selectedFeed, action) {

    var tweets = new Array();

    if (selectedFeed != -1) {
        for ( var i=0, len=feeds.length; i<len; i++) {
          if (feeds[i].id == selectedFeed) {
            feeds = [feeds[i]];
            break;
          }
        }
    }

    (function() {
      if (feeds.length > 0) {
        for (var i=0, len=feeds.length; i<len; i++) {
            (function() {
                feeds[i].getTweets(function(err, feedTweets) {
                    if (err) {
                      action(err, null);
                    } else {
                      tweets = tweets.concat(feedTweets);
                      if (i == len-1) {
                          tweets.sort(function(a,b) {
                            if (a.postdate.getTime() > b.postdate.getTime()) {
                              return -1;
                            } else if (a.postdate.getTime() < b.postdate.getTime()) {
                              return 1;
                            } else {
                              return 0;
                            }
                          });

                          (function() {
                            var unpacked = new Array();
                            for (var i=0, len=tweets.length; i<len; i++) {
                              var tweet = tweets[i];

                              tweet.getFeed(function(err, feed) {
                                feed.getUser(function(err, owner) {
                                  tweet.author = owner ? owner : { name: "No Author"};
                                  tweet.feed = feed;
                                  unpacked.push(tweet);
                                  if (i == len-1) {
                                    action(null, unpacked);
                                  }
                                });
                              });
                            }
                            action(null, tweets);
                          }());

                      }
                    }
                });
            }());
        }
      } else {
        action(null, []);
      }
    }());
  };
}

module.exports = new FeedService();
