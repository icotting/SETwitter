using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;

namespace Twitter.Application.Data
{
    public class UserRepository : EntityRepository<User>, IUserRepository
    {
        public UserRepository(IDbSetFactory dbSetFactory)
            : base(dbSetFactory)
        { }


        IQueryable<User> IEntityRepository<User>.AsQueryable()
        {
            return base.AsQueryable();
        }

        IEnumerable<User> IEntityRepository<User>.GetAll(params System.Linq.Expressions.Expression<Func<User, object>>[] includeProperties)
        {
            return base.GetAll(includeProperties);
        }

        IEnumerable<User> IEntityRepository<User>.Find(System.Linq.Expressions.Expression<Func<User, bool>> where, params System.Linq.Expressions.Expression<Func<User, object>>[] includeProperties)
        {
            return base.Find(where, includeProperties);
        }

        User IEntityRepository<User>.Find(object id)
        {
            return base.Find(id);
        }

        User IEntityRepository<User>.Single(System.Linq.Expressions.Expression<Func<User, bool>> where, params System.Linq.Expressions.Expression<Func<User, object>>[] includeProperties)
        {
            return base.Single(where, includeProperties);
        }

        User IEntityRepository<User>.First(System.Linq.Expressions.Expression<Func<User, bool>> where, params System.Linq.Expressions.Expression<Func<User, object>>[] includeProperties)
        {
            return base.First(where, includeProperties);
        }

        void IEntityRepository<User>.Delete(User entity)
        {
            base.Delete(entity);
        }

        void IEntityRepository<User>.Insert(User entity)
        {
            base.Insert(entity);
        }

        void IEntityRepository<User>.Update(User entity)
        {
            base.Update(entity);
        }

        User IEntityRepository<User>.Refresh(User entity)
        {
            return base.Refresh(entity);
        }
    }
}