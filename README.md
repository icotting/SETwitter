Java EE
==============

An example Java EE 7 Project for my Software Engineering course. The project uses Maven for dependency management and was intended to be run in Glassfish 4. The project has been tested in Netbeans 4.7, Eclipse 4.3.1 and IntelliJ 13.

The project deploys to Glassfish 4 and requires a JAAS realm configuration to support login. The following realm will work using against the included persistence unit: 

        <auth-realm name="Twitter_Realm" classname="com.sun.enterprise.security.ee.auth.realm.jdbc.JDBCRealm">
          <property name="jaas-context" value="jdbcRealm"></property>
          <property name="password-column" value="PASSWORD"></property>
          <property name="datasource-jndi" value="Twitter_ds"></property>
          <property name="group-table" value="USERPROFILE"></property>
          <property name="user-table" value="USERPROFILE"></property>
          <property name="group-name-column" value="ROLE"></property>
          <property name="digestrealm-password-enc-algorithm" value="SHA-256"></property>
          <property name="user-name-column" value="USERNAME"></property>
        </auth-realm>

The application also assumes a container configured with a JDBC connection pool bound as a JNDI resource named 
Twitter_ds. Included in the project is a glassfish-resource.xml configuration file which will setup a MySql 
connection pool as follows: 

    <jdbc-connection-pool datasource-classname="com.mysql.jdbc.jdbc2.optional.MysqlDataSource" res-type="javax.sql.DataSource" name="twitterPool">
        <property name="url" value="jdbc:mysql://localhost:3306/seTwitter"></property>
        <property name="user" value="dev"></property>
        <property name="password" value="dev"></property>
    </jdbc-connection-pool>
    <jdbc-resource pool-name="twitterPool" jndi-name="Twitter_ds"></jdbc-resource>

Unit tests run with an embedded glassfish container using Arquillian (http://arquillian.org). The default test 
configuration uses the following JDBC connection pool configuration:

    <jdbc-connection-pool datasource-classname="com.mysql.jdbc.jdbc2.optional.MysqlDataSource" res-type="javax.sql.DataSource" name="twitterPool">
        <property name="url" value="jdbc:mysql://localhost:3306/seTwitter_Test"></property>
        <property name="user" value="dev"></property>
        <property name="password" value="dev"></property>
    </jdbc-connection-pool>
    <jdbc-resource pool-name="twitterPool" jndi-name="Twitter_ds"></jdbc-resource>

The easiest configuration is to create two MySQL databases seTwitter and seTwitter_Test with user 'dev' @localhost and password 'dev' as credentials. Assuming MySQL is running on the default port, and the JAAS realm as been setup in Glassfish, everything should work as expected.
