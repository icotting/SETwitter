using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;

namespace Twitter_Shared.Data.Model
{
    public class Tweet
    {
        [Key]
        public long ID { get; set; }

        public string Content { get; set; }
        public DateTime PostDate { get; set; }
        public Feed BelongsTo { get; set; }
    }
}
