using SETwitter.Domain;
using SETwitter.Repositories.Infrastructure;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Repositories.Persistence
{
    public interface IUserRepository : IEntityRepository<User>
    {
    }
}
