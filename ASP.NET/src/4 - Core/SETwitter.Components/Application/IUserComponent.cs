using SETwitter.Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Components.Application
{
    public interface IUserComponent : IDisposable
    {
        void CreateUser(User user);
        User FindUserForName(string userName);
        bool IsValidLogin(string userName, string password);
    }
}
