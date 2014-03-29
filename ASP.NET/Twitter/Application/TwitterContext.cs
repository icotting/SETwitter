using System;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Web;
using Twitter_Shared.Data;
using Twitter_Shared.Data.Model;

namespace Twitter.Application
{
    public class TwitterContext : DbContext
    {

        public TwitterContext()
            : base("TwitterConnection")
        {

        }

        public DbSet<Tweet> Tweets { get; set; }
        public DbSet<User> Users { get; set; }
        public DbSet<Feed> Feeds { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {

            modelBuilder.Entity<User>()
                .HasMany<Feed>(u => u.Feeds)
                .WithRequired(f => f.Owner)
                .WillCascadeOnDelete(false);

            modelBuilder.Entity<Feed>()
                .HasMany<Tweet>(f => f.Tweets).WithRequired(t => t.BelongsTo).WillCascadeOnDelete(false);

            modelBuilder.Entity<Tweet>()
                .HasRequired<Feed>(t => t.BelongsTo)
                .WithMany(f => f.Tweets)
                .WillCascadeOnDelete(false);

            modelBuilder.Entity<Feed>().HasMany<User>(f => f.Subscribers).WithMany(u => u.Subscriptions).Map(m =>
            {
                m.MapLeftKey("FeedId");
                m.MapRightKey("SubscriberId");
                m.ToTable("FeedSubscriptions");
            });
        }
    }

    public class TwitterContextAdapter : IDbSetFactory, IDbContext
    {
        private readonly DbContext _context;

        public TwitterContextAdapter(DbContext context)
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
            _context.Entry<T>(entity).Reload();
        }

        #endregion
    }

}