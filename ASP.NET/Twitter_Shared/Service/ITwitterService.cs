using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Twitter_Shared.Data.Model;

namespace Twitter_Shared.Service
{
    public interface ITwitterService : IDisposable
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
