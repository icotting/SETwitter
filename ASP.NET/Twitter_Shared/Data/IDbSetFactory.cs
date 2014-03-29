using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Objects;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Twitter_Shared.Data
{
    // http://efpatterns.codeplex.com/SourceControl/changeset/view/7f1a9beddf25#Main/EntityFramework.Patterns/IObjectSetFactory.cs
    public interface IDbSetFactory : IDisposable
    {
        DbSet<T> CreateDbSet<T>() where T : class;
        void ChangeObjectState(object entity, EntityState state);
        void RefreshEntity<T>(ref T entity) where T : class;
    }
}
