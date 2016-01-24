using SETwitter.Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Components.Application
{
    public interface ITweetComponent
    {
        List<Feed> AddFeed(Feed feed);
        void AddTweet(Tweet tweet);
        void SubscribeToFeed(User user, Feed feed);
        Feed FindFeedForOwner(string feedName, User owner);

        Feed GetFeed(long id);

        List<Tweet> Search(string query);
        List<Feed> GetPossibleSubscriptionsFor(User user);
    }
}
