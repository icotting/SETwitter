using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;

namespace Twitter.Application.Data
{
    public class TweetRepository : EntityRepository<Tweet>, ITweetRepository
    {
        public TweetRepository(IDbSetFactory dbSetFactory)
            : base(dbSetFactory)
        { }


        IQueryable<Tweet> IEntityRepository<Tweet>.AsQueryable()
        {
            return base.AsQueryable();
        }

        IEnumerable<Tweet> IEntityRepository<Tweet>.GetAll(params System.Linq.Expressions.Expression<Func<Tweet, object>>[] includeProperties)
        {
            return base.GetAll(includeProperties);
        }

        IEnumerable<Tweet> IEntityRepository<Tweet>.Find(System.Linq.Expressions.Expression<Func<Tweet, bool>> where, params System.Linq.Expressions.Expression<Func<Tweet, object>>[] includeProperties)
        {
            return base.Find(where, includeProperties);
        }

        Tweet IEntityRepository<Tweet>.Find(object id)
        {
            return base.Find(id);
        }

        Tweet IEntityRepository<Tweet>.Single(System.Linq.Expressions.Expression<Func<Tweet, bool>> where, params System.Linq.Expressions.Expression<Func<Tweet, object>>[] includeProperties)
        {
            return base.Single(where, includeProperties);
        }

        Tweet IEntityRepository<Tweet>.First(System.Linq.Expressions.Expression<Func<Tweet, bool>> where, params System.Linq.Expressions.Expression<Func<Tweet, object>>[] includeProperties)
        {
            return base.First(where, includeProperties);
        }

        void IEntityRepository<Tweet>.Delete(Tweet entity)
        {
            base.Delete(entity);
        }

        void IEntityRepository<Tweet>.Insert(Tweet entity)
        {
            base.Insert(entity);
        }

        void IEntityRepository<Tweet>.Update(Tweet entity)
        {
            base.Update(entity);
        }

        Tweet IEntityRepository<Tweet>.Refresh(Tweet entity)
        {
            return base.Refresh(entity);
        }
    }
}