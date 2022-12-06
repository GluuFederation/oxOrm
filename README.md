This is the library for persistence and caching implemenations in GluuFederation. Currently LDAP, Couchbase, MySQL, Spanner are supported.

** Library has Hybrid ORM which allows to use few DB together.**

## This example shows how to configre Gluu CE with 2 SQL DBs. In this example in second DB system will store users only.

### Here are steps to confire CE with 2 SQL DBs:
1. Install CE with MySQL backend.
2. Create `/root/.mysql` and protect file with admin sql user password.
3. Dump installed DB and create new gluudb_users:
```
mysqldump -u root --password="$(cat /root/.mysql)" gluudb > gluudb.sql
mysql -u root --password="$(cat /root/.mysql)" -e "CREATE DATABASE IF NOT EXISTS gluudb_users; USE gluudb_users; SOURCE gluudb.sql;"
mysql -u root --password="$(cat /root/.mysql)" -e "GRANT ALL PRIVILEGES ON gluudb_users.* TO 'gluu'@'localhost'"
```

4. Reconfigure Gluu CE backend:
```
cp /etc/gluu/conf/gluu-sql.properties /etc/gluu/conf/gluu-sql.users.properties
chown root:gluu /etc/gluu/conf/gluu-sql.users.properties

cat > /etc/gluu/conf/gluu-hybrid.properties
storages: sql, sql.users
storage.default: sql
# Specify which base RDN we should store in sql.users backend.
storage.sql.users.mapping: people, groups
# This list can be empty. Because this backend specified as default in storage.default.
storage.sql.mapping:

chown root:gluu /etc/gluu/conf/gluu-hybrid.properties
```
```
Replace in `/etc/gluu/conf/gluu-sql.users.properties` line `db.schema.name=gluudb` with `db.schema.name=gluudb_users`
Replace in `/etc/gluu/conf/gluu.properties` line `persistence.type=sql` with `persistence.type=hybrid`
```

5. CE services restart is not needed because all services should be reloaded automatically after ORM settings in `/etc/gluu/conf/gluu.properties` update.


### These steps are needed to check configuration:
1. Log into IDP and add dummy user.
2. Run:
```
mysql -u root  -e "USE gluudb_users; SELECT uid FROM gluuPerson;" (+--password=xyz)
+-----------------------+
| uid                   |
+-----------------------+
...
| dummy                 |
+-----------------------+
```

3. Logout from IDP and try to log in as dummy user.
