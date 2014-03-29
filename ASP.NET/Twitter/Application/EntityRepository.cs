using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Objects;
using System.Linq;
using System.Linq.Expressions;
using System.Web;
using Twitter_Shared.Data;

namespace Twitter.Application
{
    public class EntityRepository<T> : IEntityRepository<T> where T : class
    {
        private readonly DbSet<T> _dbSet;
        private readonly IDbSetFactory _dbSetFactory;

        public EntityRepository(IDbSetFactory dbSetFactory)
        {
            _dbSet = dbSetFactory.CreateDbSet<T>();
            _dbSetFactory = dbSetFactory;
        }

        #region IRepository<T> Members

        public IQueryable<T> AsQueryable()
        {
            return _dbSet;
        }

        public IEnumerable<T> GetAll(params Expression<Func<T, object>>[] includeProperties)
        {
            IQueryable<T> query = AsQueryable();
            return PerformInclusions(includeProperties, query);
        }

        public IEnumerable<T> Find(Expression<Func<T, bool>> where,
                                   params Expression<Func<T, object>>[] includeProperties)
        {
            IQueryable<T> query = AsQueryable();
            query = PerformInclusions(includeProperties, query);
            return query.Where(where);
        }

        public T Find(object id)
        {
            return _dbSet.Find(id);
        }

        public T Single(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties)
        {
            IQueryable<T> query = AsQueryable();
            query = PerformInclusions(includeProperties, query);
            return query.SingleOrDefault(where);
        }

        public T First(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties)
        {
            IQueryable<T> query = AsQueryable();
            query = PerformInclusions(includeProperties, query);
            return query.FirstOrDefault(where);
        }

        public void Delete(T entity)
        {
            _dbSetFactory.ChangeObjectState(entity, EntityState.Deleted);
        }

        public void Insert(T entity)
        {
            _dbSet.Add(entity);
        }

        public void Update(T entity)
        {
            _dbSetFactory.ChangeObjectState(entity, EntityState.Modified);
        }

        public T Refresh(T entity)
        {
            _dbSetFactory.RefreshEntity(ref entity);
            return entity;
        }

        #endregion

        private static IQueryable<T> PerformInclusions(IEnumerable<Expression<Func<T, object>>> includeProperties,
                                                       IQueryable<T> query)
        {
            return includeProperties.Aggregate(query, (current, includeProperty) => current.Include(includeProperty));
        }
    }
}