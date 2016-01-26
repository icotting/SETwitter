using Microsoft.Data.Entity;
using Microsoft.Extensions.DependencyInjection;
using SETwitter.Persistence.Infrastructure.EntityFramework;
using SETwitter.Repositories.Infrastructure;
using SETwitter.Repositories.Persistence;
using Microsoft.Extensions.Configuration;

namespace SETwitter.Persistence.DependencyInjection
{
    public static class PersistenceServiceCollectionExtensions
    {
        public static IServiceCollection AddPersistence(this IServiceCollection serviceCollection, 
            IConfiguration configuration)
        {
            serviceCollection.AddEntityFramework().AddSqlite().
                AddDbContext<PersistenceContext>(options =>
                    options.UseSqlite(configuration["Data:DefaultConnection:ConnectionString"]));

            serviceCollection.AddTransient<ITweetRepository, TweetRepository>();
            serviceCollection.AddTransient<IFeedRepository, FeedRepository>();
            serviceCollection.AddTransient<IUserRepository, UserRepository>();

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
