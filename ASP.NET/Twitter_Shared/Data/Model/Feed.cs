using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;

namespace Twitter_Shared.Data.Model
{
    public class Feed
    {
        [Key]
        public long ID { get; set; }

        public string Name { get; set; }
        public virtual ICollection<Tweet> Tweets { get; set; }
        public virtual ICollection<User> Subscribers { get; set; }
        public User Owner { get; set; }
    }
}
