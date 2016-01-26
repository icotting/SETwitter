using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Domain
{
    public class FeedSubscription
    {
        public long UserId { get; set; }
        public User User { get; set; }
        public long FeedId { get; set; }
        public Feed Feed { get; set; }
    }
}
