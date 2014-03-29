using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;
using Twitter_Shared.Service;

namespace Twitter.Application.Service
{
    public class UserService : IUserService
    {
        private IEntityRepository<User> _userRepository;

        public UserService(IEntityRepository<User> userRepository)
        {
            _userRepository = userRepository;
        }

        public void CreateUser(Twitter_Shared.Data.Model.User user)
        {
            _userRepository.Insert(user);
        }

        public Twitter_Shared.Data.Model.User FindUserForName(string userName)
        {
            return _userRepository.First(u => u.UserName == userName, u => u.Subscriptions, u => u.Feeds);
        }

        public bool IsValidLogin(string userName, string password)
        {
            return (_userRepository.Find(u => u.UserName == userName && u.Password == password).Count() != 0);
        }

        /* "standard" C# thread safe dispose pattern */
        private bool _disposed;
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~UserService()
        {
            Dispose(false);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (_disposed)
                return;

            if (disposing)
            {
                
            }
            _disposed = true;
        }
    }
}