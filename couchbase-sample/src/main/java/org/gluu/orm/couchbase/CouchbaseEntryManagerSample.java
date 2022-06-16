/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.impl.CouchbaseEntryManagerFactory;

/**
 * @author Yuriy Movchan
 * Date: 01/13/2017
 */
public class CouchbaseEntryManagerSample {

    private static final Logger LOG = Logger.getLogger(CouchbaseEntryManagerSample.class);

    private Properties getSampleConnectionProperties() {
        Properties connectionProperties = new Properties();

        connectionProperties.put("couchbase#servers", "localhost");
        connectionProperties.put("couchbase#auth.userName", "admin");
        connectionProperties.put("couchbase#auth.userPassword", "secret");

        connectionProperties.put("couchbase#connection.dns.use-lookup", "false");
        
        connectionProperties.put("couchbase#buckets", "gluu, gluu_user, gluu_site, gluu_cache, gluu_token, gluu_session");

        connectionProperties.put("couchbase#bucket.default", "gluu");
        connectionProperties.put("couchbase#bucket.gluu_user.mapping", "people, groups, authorizations");
        connectionProperties.put("couchbase#bucket.gluu_site.mapping", "cache-refresh");
        connectionProperties.put("couchbase#bucket.gluu_cache.mapping", "cache");
        connectionProperties.put("couchbase#bucket.gluu_token.mapping", "tokens");
        connectionProperties.put("couchbase#bucket.gluu_session.mapping", "sessions");
        
        connectionProperties.put("couchbase#password.encryption.method", "SSHA-256");

        return connectionProperties;
    }

    public CouchbaseEntryManager createCouchbaseEntryManager() {
        CouchbaseEntryManagerFactory couchbaseEntryManagerFactory = new CouchbaseEntryManagerFactory();
        couchbaseEntryManagerFactory.create();
        Properties connectionProperties = getSampleConnectionProperties();

        CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerFactory.createEntryManager(connectionProperties);
        LOG.debug("Created CouchbaseEntryManager: " + couchbaseEntryManager);

        return couchbaseEntryManager;
    }

}