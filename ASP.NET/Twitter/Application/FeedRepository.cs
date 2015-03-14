using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;

namespace Twitter.Application.Data
{
    public class FeedRepository : EntityRepository<Feed>, IFeedRepository
    {
        public FeedRepository(IDbSetFactory dbSetFactory)
            : base(dbSetFactory)
        {

        }
       
        IQueryable<Feed> IEntityRepository<Feed>.AsQueryable()
        {
            return base.AsQueryable();
        }

        IEnumerable<Feed> IEntityRepository<Feed>.GetAll(params System.Linq.Expressions.Expression<Func<Feed, object>>[] includeProperties)
        {
            return base.GetAll(includeProperties);
        }

        IEnumerable<Feed> IEntityRepository<Feed>.Find(System.Linq.Expressions.Expression<Func<Feed, bool>> where, params System.Linq.Expressions.Expression<Func<Feed, object>>[] includeProperties)
        {
            return base.Find(where, includeProperties);
        }

        Feed IEntityRepository<Feed>.Find(object id)
        {
            return base.Find(id);
        }

        Feed IEntityRepository<Feed>.Single(System.Linq.Expressions.Expression<Func<Feed, bool>> where, params System.Linq.Expressions.Expression<Func<Feed, object>>[] includeProperties)
        {
            return base.Single(where, includeProperties);
        }

        Feed IEntityRepository<Feed>.First(System.Linq.Expressions.Expression<Func<Feed, bool>> where, params System.Linq.Expressions.Expression<Func<Feed, object>>[] includeProperties)
        {
            return base.First(where, includeProperties);
        }

        void IEntityRepository<Feed>.Delete(Feed entity)
        {
            base.Delete(entity);
        }

        void IEntityRepository<Feed>.Insert(Feed entity)
        {
            base.Insert(entity);
        }

        void IEntityRepository<Feed>.Update(Feed entity)
        {
            base.Update(entity);
        }

        Feed IEntityRepository<Feed>.Refresh(Feed entity)
        {
            return base.Refresh(entity);
        }
    }
}