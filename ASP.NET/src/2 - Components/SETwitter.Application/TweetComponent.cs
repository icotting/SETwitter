using SETwitter.Components.Application;
using SETwitter.Domain;
using SETwitter.Repositories.Persistence;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Application
{
    public class TweetComponent : ITweetComponent
    {
        private IFeedRepository _feedRepository;
        private IUserRepository _userRepository;
        private ITweetRepository _tweetRepository;

        /* The repository values will be injected into the class when 
         * the dependency injection framework calls the constructor
         */
        public TweetComponent(IFeedRepository feedRepository,
            IUserRepository userRepository,
            ITweetRepository tweetRepository)
        {
            _userRepository = userRepository;
            _feedRepository = feedRepository;
            _tweetRepository = tweetRepository;
        }

        public List<Feed> AddFeed(Feed feed)
        {
            _feedRepository.Insert(feed);
            return _userRepository.Refresh(feed.Owner).Feeds.ToList<Feed>();
        }

        public void AddTweet(Tweet tweet)
        {
            _tweetRepository.Insert(tweet);
        }

        public void SubscribeToFeed(User user, Feed feed)
        {
            if (user.Subscriptions == null)
            {
                user.Subscriptions = new List<FeedSubscription>();
            }
            user.Subscriptions.Add(new FeedSubscription() { Feed = feed, FeedId = feed.Id, User = user, UserId = user.Id });
            _userRepository.Update(user);
        }

        public Feed GetFeed(long id)
        {
            return _feedRepository.Find(id);
        }

        public List<Tweet> Search(string query)
        {
            string[] terms = query.Split(new[] { ' ' });
            return _tweetRepository.Find(tweet => terms.All(t => tweet.Content.Contains(t)),
                tweet => tweet.BelongsTo,
                tweet => tweet.BelongsTo.Owner)
                    .OrderByDescending(tweet => tweet.PostDate).ToList<Tweet>();
        }

        public List<Feed> GetPossibleSubscriptionsFor(User user)
        {
            List<Feed> feeds = _feedRepository.Find(f => f.Owner.Id != user.Id, f => f.Owner, f => f.Subscribers).ToList<Feed>();
            List<Feed> filtered = new List<Feed>();

            foreach (Feed f in feeds)
            {
                if (f.Subscribers.Where(s => s.UserId == user.Id).Count() == 0)
                {
                    filtered.Add(f);
                }
            }

            return filtered;
        }

        public Feed FindFeedForOwner(string feedName, User owner)
        {
            return _feedRepository.Find(f => f.Owner.Id == owner.Id && f.Name.Equals(feedName)).FirstOrDefault();
        }


        /* "standard" C# thread safe dispose pattern */
        private bool _disposed;
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~TweetComponent()
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
