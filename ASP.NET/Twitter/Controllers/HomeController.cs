using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using Twitter.Application;
using Twitter.Models.Home;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;
using Twitter_Shared.Service;

namespace Twitter.Controllers
{
    public class HomeController : Controller
    {
        private IUnitOfWork _unit;
        private ITwitterService _twitterService;
        private IUserService _userService;

        public HomeController(IUnitOfWork unit, ITwitterService twitterService, IUserService userService)
        {
            _unit = unit;
            _twitterService = twitterService;
            _userService = userService;
        }

        [Authorize]
        public ActionResult Index(long? selectedFeed)
        {
            HomeViewModel model = GenerateIndexModel(selectedFeed.HasValue ? selectedFeed.Value : -1);
            return View(model);
        }

        [Authorize]
        [HttpPost, ActionName("Tweet")]
        public PartialViewResult Tweet(HomeViewModel model)
        {
            Tweet tweet = new Tweet()
            {
                BelongsTo = _twitterService.GetFeed(Convert.ToInt64(model.SelectedFeed)),
                Content = model.Tweet,
                PostDate = DateTime.Now
            };
            _twitterService.AddTweet(tweet);
            _unit.Commit();

            HomeViewModel updatedMode = GenerateIndexModel(model.DisplayFeed);
            updatedMode.Tweet = "";
            updatedMode.SelectedFeed = model.SelectedFeed;
            ModelState.Clear();
            return PartialView("_TweetView", updatedMode);
        }

        [Authorize]
        [HttpPost]
        public PartialViewResult CreateFeed(HomeViewModel model)
        {
            // get a user that matches the name of the currently logged in user
            User user = _userService.FindUserForName(User.Identity.Name);
            Feed feed = new Feed()
            {
                Name = model.NewFeedName,
                Owner = user
            };
            model.Feeds = _twitterService.AddFeed(feed);
            _unit.Commit();

            model.NewFeedName = "";
            ModelState.Clear();
            return PartialView("_FeedView", model);
        }

        [Authorize]
        [HttpPost]
        public PartialViewResult SubscribeToFeed(string subscribeTo)
        {
            if (subscribeTo != null)
            {
                User user = _userService.FindUserForName(User.Identity.Name);

                string[] parts = subscribeTo.Split(new string[] { "by " }, StringSplitOptions.None);
                User owner = _userService.FindUserForName(parts[1].Trim().Substring(1));
                Debug.Assert(owner != null);
                
                Feed feed = _twitterService.FindFeedForOwner(parts[0].Trim(), owner);
                Debug.Assert(feed != null);

                _twitterService.SubscribeToFeed(user, feed);

                _unit.Commit();
            }

            return PartialView("Index", GenerateIndexModel(-1));
        }

        [Authorize]
        [HttpPost, ActionName("Search")]
        public PartialViewResult Search(string searchQuery)
        {
            HomeViewModel model = GenerateIndexModel(-1);
            model.Tweets = _twitterService.Search(searchQuery);
            model.DisplayFeed = -1;

            return PartialView("_TweetView", model);
        }

        [Authorize]
        [HttpGet, ActionName("LogOFf")]
        public ActionResult LogOff()
        {
            FormsAuthentication.SignOut();
            return RedirectToAction("Index");
        }

        /* this entire process of setting up the models should be refactored 
         * in terms of partitioning the models and partial views */
        private HomeViewModel GenerateIndexModel(long displayFeed)
        {
            HomeViewModel model = new HomeViewModel();

                // get a user that matches the name of the currently logged in user
                User user = _userService.FindUserForName(User.Identity.Name);

                /* Find all of the tweets that should be displayed. The tweets that 
                 * should be displayed are either authored by the user or those that 
                 * are found in a user subscription */
                List<Tweet> tweetsToDisplay = new List<Tweet>();

                if (displayFeed == -1)
                {
                    if (user.Feeds != null)
                    {
                        foreach (Feed feed in user.Feeds)
                        {
                            if (feed.Tweets != null && feed.Tweets.Count > 0)
                            {
                                tweetsToDisplay.AddRange(feed.Tweets);
                            }
                        }
                    }

                    if (user.Subscriptions != null)
                    {
                        foreach (Feed feed in user.Subscriptions)
                        {
                            if (feed.Tweets != null && feed.Tweets.Count > 0)
                            {
                                tweetsToDisplay.AddRange(feed.Tweets);
                            }
                        }
                    }
                }
                else
                {
                    Feed feed = _twitterService.GetFeed(displayFeed);
                    tweetsToDisplay = (feed == null) ? new List<Tweet>() : feed.Tweets.ToList<Tweet>();
                }
                model.DisplayFeed = displayFeed;

                model.Tweets = tweetsToDisplay.OrderByDescending(t => t.PostDate).ToList<Tweet>();

                if (user.Feeds != null && user.Feeds.Count > 0)
                {
                    model.HasFeeds = true;
                }

                // Add the user's feeds to the feed model that will be rendered as a partial view
                model.Feeds = user.Feeds == null ? new List<Feed>() : user.Feeds;
                model.Feeds = model.Feeds.OrderBy(f => f.Name).ToList();

                model.Subscriptions = user.Subscriptions == null ? new List<Feed>() : user.Subscriptions;

                StringBuilder sb = new StringBuilder();
                sb.Append("[");

                List<Feed> possible = _twitterService.GetPossibleSubscriptionsFor(user);
                foreach (Feed feed in possible)
                {
                    string subscription = string.Format("{{value: \"{0} by @{1}\", tokens: [\"{2}\",\"{3}\",\"{4}\"]}}", feed.Name, feed.Owner.UserName, 
                        feed.Owner.Name.Split(' ')[0], feed.Owner.Name.Split(' ')[1], feed.Name);

                    sb.Append(string.Format("{0},", subscription));
                }
                if (possible.Count > 0)
                {
                    sb.Replace(',', ']', sb.Length - 1, 1);
                }
                else
                {
                    sb.Append("]");
                }
                model.SubscriptionTypeahead = sb.ToString();
            
            model.FeedList = model.Feeds.Select(f => new SelectListItem() { Text = f.Name, Value = Convert.ToString(f.ID) });

            return model;
        }
    }
}
