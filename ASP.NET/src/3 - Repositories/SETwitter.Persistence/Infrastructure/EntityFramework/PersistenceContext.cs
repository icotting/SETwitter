using Microsoft.Data.Entity;
using Microsoft.Data.Entity.Infrastructure;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Analytics.Persistence.Infrastructure.EntityFramework
{
    public class PersistenceContext : DbContext
    {
        public PersistenceContext() { }

        public PersistenceContext(DbContextOptions options) : base(options) { }

        public PersistenceContext(IServiceProvider serviceProvider) : base(serviceProvider) { }

        public PersistenceContext(IServiceProvider serviceProvider, DbContextOptions options)
            : base(serviceProvider, options)
        { }
    }

    public class PersistenceContextAdapter : IDbSetFactory, IDbContext
    {
        private readonly DbContext _context;

        public PersistenceContextAdapter(DbContext context)
        {
            _context = context;
        }

        #region IObjectContext Members

        public void SaveChanges()
        {
            _context.SaveChanges();
        }

        #endregion

        #region IObjectSetFactory Members

        public void Dispose()
        {
            _context.Dispose();
        }

        public DbSet<T> CreateDbSet<T>() where T : class
        {
            return _context.Set<T>();
        }

        public void ChangeObjectState(object entity, EntityState state)
        {
            _context.Entry(entity).State = state;
        }

        public void RefreshEntity<T>(ref T entity) where T : class
        {
            // TODO: implement when EF7 functionality exists
        }
        #endregion
    }
}
