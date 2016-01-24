
using System.Collections.Generic;

namespace SETwitter.Domain
{
    public class Feed
    {
        public long Id { get; set; }

        public string Name { get; set; }
        public virtual ICollection<Tweet> Tweets { get; set; }
        public virtual ICollection<User> Subscribers { get; set; }
        public User Owner { get; set; }
    }
}