/*
 * Geddy JavaScript Web development framework
 * Copyright 2112 Matthew Eernisse (mde@fleegix.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/

var auth = require('../helpers/auth')
  , requireAuth = auth.requireAuth;

var userservice = require('../services/userservice');
var feedservice = require('../services/feedservice');

var Main = function () {

	this.before(requireAuth, {});

	this.index = function (req, resp, params) {
    var self = this;
    userservice.loadUserFromSession(self.session, function(err, user) {
      if (user) {
        user.getFeeds(function(err, feeds) {
          var selectedFeed = params['selectedFeed'] ? params['selectedFeed'] : -1;
          feedservice.getTweetsToDisplay(feeds, selectedFeed, function(err, tweets) {
            if (err) {
              // do something
            } else {
              self.respond({params: params, user: user, feeds: feeds, tweets: tweets, selectedFeed: selectedFeed}, {
                format: 'html'
                , template: 'app/views/main/index'
              });
            }
          });
        });
      }
    });
	};
}

exports.Main = Main;
