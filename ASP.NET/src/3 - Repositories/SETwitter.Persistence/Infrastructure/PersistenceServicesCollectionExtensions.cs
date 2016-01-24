using Analytics.Persistence.Infrastructure.EntityFramework;
using Microsoft.Data.Entity;
using Microsoft.Extensions.DependencyInjection;
using SETwitter.Repositories.Infrastructure;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Persistence.DependencyInjection
{
    public static class PersistenceServicesCollectionExtensions
    {
        public static IServiceCollection AddPersistence(this IServiceCollection serviceCollection)
        {

            /* TODO add repository bindings here */

            /* Setup Infrastructure Bindings */
            serviceCollection.AddTransient<DbContext, PersistenceContext>();
            serviceCollection.AddTransient<IUnitOfWork, UnitOfWork>();

            serviceCollection.AddScoped(p => new PersistenceContextAdapter(p.GetService<DbContext>()));

            serviceCollection.AddTransient(typeof(IDbContext), p => p.GetService<PersistenceContextAdapter>());
            serviceCollection.AddTransient(typeof(IDbSetFactory), p => p.GetService<PersistenceContextAdapter>());

            return serviceCollection;
        }
    }
}
