using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Twitter_Shared.Data.Model;

namespace Twitter_Shared.Service
{
    public interface IUserService : IDisposable
    {
        void CreateUser(User user);
        User FindUserForName(string userName);

        bool IsValidLogin(string userName, string password);
    }
}
