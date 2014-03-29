using System.Web.Mvc;
using Microsoft.Practices.Unity;
using Unity.Mvc3;
using System.Web.Http;
using Microsoft.Practices.ServiceLocation;
using Twitter_Shared.Data;
using System.Data.Entity;
using Twitter.Application.Service;
using Twitter_Shared.Service;

namespace Twitter.Application
{
    public static class Bootstrapper
    {
        public static void Initialise()
        {
            var container = BuildUnityContainer();

            DependencyResolver.SetResolver(new UnityDependencyResolver(container));
            GlobalConfiguration.Configuration.DependencyResolver = new Unity.WebApi.UnityDependencyResolver(container);
            ServiceLocator.SetLocatorProvider(() => new UnityServiceLocator(container));
        }

        private static IUnityContainer BuildUnityContainer()
        {
            var container = new UnityContainer();

            /* register the data related components with Unity */
            container.RegisterType(typeof(IUnitOfWork), typeof(UnitOfWork));
            container.RegisterType(typeof(IEntityRepository<>), typeof(EntityRepository<>));
            container.RegisterType(typeof(DbContext), typeof(TwitterContext));

            /* register the services with Unity */
            container.RegisterType(typeof(IUserService), typeof(UserService));
            container.RegisterType(typeof(ITwitterService), typeof(TwitterService));

            /* setup the shared data context for the unit of work */
            container.RegisterInstance(new TwitterContextAdapter(container.Resolve<DbContext>()), new HierarchicalLifetimeManager());
            container.RegisterType<IDbSetFactory>(new InjectionFactory(con => con.Resolve<TwitterContextAdapter>()));
            container.RegisterType<IDbContext>(new InjectionFactory(con => con.Resolve<TwitterContextAdapter>()));

            return container;
        }
    }
}