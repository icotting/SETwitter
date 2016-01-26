using System;
using Microsoft.Data.Entity;
using Microsoft.Data.Entity.Infrastructure;
using Microsoft.Data.Entity.Metadata;
using Microsoft.Data.Entity.Migrations;
using SETwitter.Persistence;

namespace SETwitter.Persistence.Migrations
{
    [DbContext(typeof(PersistenceContext))]
    [Migration("20160126040351_InitialMigration")]
    partial class InitialMigration
    {
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
            modelBuilder
                .HasAnnotation("ProductVersion", "7.0.0-rc1-16348");

            modelBuilder.Entity("SETwitter.Domain.Feed", b =>
                {
                    b.Property<long>("Id")
                        .ValueGeneratedOnAdd();

                    b.Property<string>("Name");

                    b.Property<long?>("OwnerId");

                    b.HasKey("Id");
                });

            modelBuilder.Entity("SETwitter.Domain.FeedSubscription", b =>
                {
                    b.Property<long>("Id")
                        .ValueGeneratedOnAdd();

                    b.Property<long>("FeedId");

                    b.Property<long>("UserId");

                    b.HasKey("Id");
                });

            modelBuilder.Entity("SETwitter.Domain.Tweet", b =>
                {
                    b.Property<long>("Id")
                        .ValueGeneratedOnAdd();

                    b.Property<long?>("BelongsToId");

                    b.Property<string>("Content");

                    b.Property<DateTime>("PostDate");

                    b.HasKey("Id");
                });

            modelBuilder.Entity("SETwitter.Domain.User", b =>
                {
                    b.Property<long>("Id")
                        .ValueGeneratedOnAdd();

                    b.Property<string>("Email");

                    b.Property<string>("Location");

                    b.Property<string>("Name");

                    b.Property<string>("Password");

                    b.Property<string>("UserName");

                    b.HasKey("Id");
                });

            modelBuilder.Entity("SETwitter.Domain.Feed", b =>
                {
                    b.HasOne("SETwitter.Domain.User")
                        .WithMany()
                        .HasForeignKey("OwnerId");
                });

            modelBuilder.Entity("SETwitter.Domain.FeedSubscription", b =>
                {
                    b.HasOne("SETwitter.Domain.Feed")
                        .WithMany()
                        .HasForeignKey("FeedId");

                    b.HasOne("SETwitter.Domain.User")
                        .WithMany()
                        .HasForeignKey("UserId");
                });

            modelBuilder.Entity("SETwitter.Domain.Tweet", b =>
                {
                    b.HasOne("SETwitter.Domain.Feed")
                        .WithMany()
                        .HasForeignKey("BelongsToId");
                });
        }
    }
}
