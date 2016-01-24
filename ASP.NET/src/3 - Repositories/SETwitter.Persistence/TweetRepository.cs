using Analytics.Persistence.Infrastructure.EntityFramework;
using SETwitter.Domain;
using SETwitter.Repositories.Persistence;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Persistence
{
    public class TweetRepository : EntityRepository<Tweet>, ITweetRepository
    {
        public TweetRepository(IDbSetFactory dbSetFactory)
            : base(dbSetFactory)
        {

        }
    }
}
