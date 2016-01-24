using System.Collections.Generic;

namespace SETwitter.Domain
{
    public class User
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Location { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string UserName { get; set; }
        public virtual ICollection<Feed> Feeds { get; set; }
        public virtual ICollection<Feed> Subscriptions { get; set; }
    }
}