using Microsoft.Data.Entity;
using Microsoft.Data.Entity.Infrastructure;
using SETwitter.Domain;
using SETwitter.Persistence.Infrastructure.EntityFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SETwitter.Persistence
{
    public class PersistenceContext : DbContext
    {
        public PersistenceContext() { }

        public PersistenceContext(DbContextOptions options) : base(options) { }

        public PersistenceContext(IServiceProvider serviceProvider) : base(serviceProvider) { }

        public PersistenceContext(IServiceProvider serviceProvider, DbContextOptions options)
            : base(serviceProvider, options)
        { }

        public DbSet<Feed> Feeds { get; set; }
        public DbSet<Tweet> Tweets { get; set; }
        public DbSet<User> Users { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Tweet>().HasKey(t => t.Id);
            modelBuilder.Entity<Feed>().HasKey(f => f.Id);
            modelBuilder.Entity<User>().HasKey(u => u.Id);
            modelBuilder.Entity<FeedSubscription>().HasKey(s => s.Id);

            modelBuilder.Entity<User>()
                .HasMany<Feed>(u => u.Feeds)
                .WithOne(f => f.Owner).OnDelete(Microsoft.Data.Entity.Metadata.DeleteBehavior.Cascade);

            modelBuilder.Entity<Feed>()
                .HasMany<Tweet>(f => f.Tweets).WithOne(t => t.BelongsTo).OnDelete(Microsoft.Data.Entity.Metadata.DeleteBehavior.Cascade);

            modelBuilder.Entity<FeedSubscription>().HasOne<User>(s => s.User).WithMany(u => u.Subscriptions).HasForeignKey(s => s.UserId);
            modelBuilder.Entity<FeedSubscription>().HasOne<Feed>(s => s.Feed).WithMany(f => f.Subscribers).HasForeignKey(s => s.FeedId);

            modelBuilder.Entity<Tweet>().HasOne<Feed>(t => t.BelongsTo).WithMany(f => f.Tweets).OnDelete(Microsoft.Data.Entity.Metadata.DeleteBehavior.Restrict);
        }
    }

    public class PersistenceContextAdapter : IDbSetFactory, IDbContext
    {
        private readonly DbContext _context;

        public PersistenceContextAdapter(DbContext context)
        {
            _context = context;
        }

        #region IObjectContext Members

        public void SaveChanges()
        {
            _context.SaveChanges();
        }

        #endregion

        #region IObjectSetFactory Members

        public void Dispose()
        {
            _context.Dispose();
        }

        public DbSet<T> CreateDbSet<T>() where T : class
        {
            return _context.Set<T>();
        }

        public void ChangeObjectState(object entity, EntityState state)
        {
            _context.Entry(entity).State = state;
        }

        public void RefreshEntity<T>(ref T entity) where T : class
        {
            // TODO: implement when EF7 functionality exists
        }
        #endregion
    }
}
