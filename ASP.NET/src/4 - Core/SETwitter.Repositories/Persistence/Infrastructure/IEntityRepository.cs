using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace SETwitter.Repositories.Infrastructure
{
    public interface IEntityRepository<T> where T : class
    {
        IQueryable<T> AsQueryable();
        IEnumerable<T> GetAll(params Expression<Func<T, object>>[] includeProperties);
        T Find(object id);
        IEnumerable<T> Find(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties);
        T Single(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties);
        T First(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties);
        bool Contains(object id);
        bool Contains(T entity);
        void Delete(T entity);
        T Insert(T entity);
        void Update(T entity);
        T Refresh(T entity);
    }
}
