using System;
using Microsoft.Data.Entity;

namespace Analytics.Persistence.Infrastructure.EntityFramework
{
    // http://efpatterns.codeplex.com/SourceControl/changeset/view/7f1a9beddf25#Main/EntityFramework.Patterns/IObjectSetFactory.cs
    public interface IDbSetFactory : IDisposable
    {
        DbSet<T> CreateDbSet<T>() where T : class;
        void ChangeObjectState(object entity, EntityState state);
        void RefreshEntity<T>(ref T entity) where T : class;
    }
}
