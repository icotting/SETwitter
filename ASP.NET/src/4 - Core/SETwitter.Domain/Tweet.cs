using System;

namespace SETwitter.Domain
{
    public class Tweet
    {
        public long Id { get; set; }
        public string Content { get; set; }
        public DateTime PostDate { get; set; }
        public Feed BelongsTo { get; set; }
    }
}