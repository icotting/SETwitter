using Microsoft.Extensions.DependencyInjection;
using SETwitter.Components.Application;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Application.Infrastructure
{
    public static class ApplicationServiceCollectionExtensions
    {
        public static IServiceCollection AddApplicationComponents(this IServiceCollection serviceCollection)
        {
            serviceCollection.AddTransient<ITweetComponent, TweetComponent>();
            serviceCollection.AddTransient<IUserComponent, UserComponent>();

            return serviceCollection;
        }
    }
}
