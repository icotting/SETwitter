using SETwitter.Domain;
using SETwitter.Persistence.Infrastructure.EntityFramework;
using SETwitter.Repositories.Persistence;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Persistence
{
    public class FeedRepository : EntityRepository<Feed>, IFeedRepository
    {
        public FeedRepository(IDbSetFactory dbSetFactory)
            : base(dbSetFactory)
        {

        }
    }
}
