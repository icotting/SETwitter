using SETwitter.Repositories.Infrastructure;
using System;

namespace Analytics.Persistence.Infrastructure.EntityFramework
{
    public class UnitOfWork : IUnitOfWork, IDisposable
    {
        private readonly IDbContext _dbContext;

        public UnitOfWork(IDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        #region IDisposable Members

        public void Dispose()
        {
            if (_dbContext != null)
            {
                _dbContext.Dispose();
            }

            GC.SuppressFinalize(this);
        }

        #endregion

        #region IUnitOfWork Members

        public void Commit()
        {
            _dbContext.SaveChanges();
        }

        #endregion
    }
}