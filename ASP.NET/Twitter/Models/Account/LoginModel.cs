using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Twitter.Models.Account
{
    public class LoginModel
    {
        public UserModel NewUser { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }
    }
}