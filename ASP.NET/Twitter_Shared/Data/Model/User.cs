using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;

namespace Twitter_Shared.Data.Model
{
    public class User
    {
        [Key]
        public long ID { get; set; }
        public string Name { get; set; }
        public string Location { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string UserName { get; set; }
        public virtual ICollection<Feed> Feeds { get; set; }
        public virtual ICollection<Feed> Subscriptions { get; set; }
    }
}
