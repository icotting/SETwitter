using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using Twitter.Application;
using Twitter.Models.Account;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;
using Twitter_Shared.Service;

namespace Twitter.Controllers
{
    public class LoginController : Controller
    {
        private IUserService _userService;
        private IUnitOfWork _unit; 

        public LoginController(IUserService userService, IUnitOfWork unit)
        {
            _userService = userService;
            _unit = unit;
        }

        public ActionResult Index()
        {
            return View(new LoginModel());
        }

        [HttpPost, ActionName("RegisterUser")]
        public ActionResult RegisterUser(LoginModel model)
        {
            if ( ModelState.IsValid)
            {
                User newUser = new User()
                {
                    Email = model.NewUser.Email,
                    Location = model.NewUser.Location,
                    Name = model.NewUser.Name,
                    Password = model.NewUser.Password, 
                    UserName = model.NewUser.UserName
                };
                _userService.CreateUser(newUser);
                _unit.Commit();

                FormsAuthentication.SetAuthCookie(model.NewUser.UserName, false);

                return RedirectToAction("Index", "Home");
            } 
            else 
            {
                return RedirectToAction("Index", "Login", model);
            }
        }

        [HttpPost]
        public ActionResult Index(LoginModel model, string returnUrl)
        {
            if (_userService.IsValidLogin(model.Username, model.Password))
            {
                FormsAuthentication.SetAuthCookie(model.Username, false);
                if (returnUrl != null)
                {
                    return Redirect(returnUrl);
                }
                else
                {
                    return RedirectToAction("Index", "Home");
                }
            }
            else
            {
                ModelState.AddModelError("result", "Incorrect user name or password.  Try again.");
                return View(model);
            }
        }

    }
}
