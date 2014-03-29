using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;
using Twitter_Shared.Service;

namespace Twitter.Application.Service
{
    public class TwitterService : ITwitterService
    {
        private IEntityRepository<Feed> _feedRepository;
        private IEntityRepository<User> _userRepository;
        private IEntityRepository<Tweet> _tweetRepository;

        /* The repository values will be injected into the class when 
         * the dependency injection framework calls the constructor
         */
        public TwitterService(IEntityRepository<Feed> feedRepository, 
            IEntityRepository<User> userRepository, 
            IEntityRepository<Tweet> tweetRepository)
        {
            _userRepository = userRepository;
            _feedRepository = feedRepository;
            _tweetRepository = tweetRepository;
        }

        public List<Twitter_Shared.Data.Model.Feed> AddFeed(Twitter_Shared.Data.Model.Feed feed)
        {
            _feedRepository.Insert(feed);
            return _userRepository.Refresh(feed.Owner).Feeds.ToList<Feed>();
        }

        public void AddTweet(Twitter_Shared.Data.Model.Tweet tweet)
        {
            _tweetRepository.Insert(tweet);
        }

        public void SubscribeToFeed(Twitter_Shared.Data.Model.User user, Twitter_Shared.Data.Model.Feed feed)
        {
            if (user.Subscriptions == null)
            {
                user.Subscriptions = new List<Feed>();
            }
            user.Subscriptions.Add(feed);
            _userRepository.Update(user);
        }

        public Twitter_Shared.Data.Model.Feed GetFeed(long id)
        {
            return _feedRepository.Find(id);
        }

        public List<Twitter_Shared.Data.Model.Tweet> Search(string query)
        {
            string[] terms = query.Split(new[] { ' ' });
            return _tweetRepository.Find(tweet => terms.All(t => tweet.Content.Contains(t)), 
                tweet => tweet.BelongsTo, 
                tweet => tweet.BelongsTo.Owner)
                    .OrderByDescending(tweet => tweet.PostDate).ToList<Tweet>();
        }

        public List<Feed> GetPossibleSubscriptionsFor(User user)
        {
            List<Feed> feeds = _feedRepository.Find(f => f.Owner.ID != user.ID, f => f.Owner, f => f.Subscribers).ToList<Feed>();
            List<Feed> filtered = new List<Feed>();

            foreach (Feed f in feeds)
            {
                if (f.Subscribers.Contains(user) == false)
                {
                    filtered.Add(f);
                }
            }

            return filtered;
        }

        public Feed FindFeedForOwner(string feedName, User owner)
        {
            return _feedRepository.Find(f => f.Owner.ID == owner.ID && f.Name.Equals(feedName)).FirstOrDefault();
        }


        /* "standard" C# thread safe dispose pattern */
        private bool _disposed;
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~TwitterService()
        {
            Dispose(false);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed)
                return;

            if (disposing)
            {
                
            }
            _disposed = true;
        }
    }
}