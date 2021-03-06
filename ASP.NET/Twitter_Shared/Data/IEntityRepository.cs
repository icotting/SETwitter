﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;

namespace Twitter_Shared.Data
{
    public interface IEntityRepository<T> where T : class
    {
        IQueryable<T> AsQueryable();
        IEnumerable<T> GetAll(params Expression<Func<T, object>>[] includeProperties);
        IEnumerable<T> Find(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties);
        T Find(object id);
        T Single(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties);
        T First(Expression<Func<T, bool>> where, params Expression<Func<T, object>>[] includeProperties);
        void Delete(T entity);
        void Insert(T entity);
        void Update(T entity);
        T Refresh(T entity);
    }
}
