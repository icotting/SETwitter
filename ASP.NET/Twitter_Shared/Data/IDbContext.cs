using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Twitter_Shared.Data
{
    // http://efpatterns.codeplex.com/SourceControl/changeset/view/7f1a9beddf25#Main/EntityFramework.Patterns/IObjectContext.cs
    public interface IDbContext : IDisposable
    {
        void SaveChanges();
    }
}
