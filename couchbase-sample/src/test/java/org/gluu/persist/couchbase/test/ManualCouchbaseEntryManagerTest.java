/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.couchbase.test;

import org.gluu.persist.exception.operation.SearchException;
import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.impl.CouchbaseEntryManagerFactory;
import org.gluu.orm.couchbase.model.SimpleClient;
import org.gluu.orm.util.Pair;
import org.testng.annotations.Test;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetOptions;
import com.couchbase.client.java.kv.GetResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Yuriy Zabrovarnyy
 */
public class ManualCouchbaseEntryManagerTest {

    @Test(enabled = true) // manual
    public void sample() throws IOException {
        CouchbaseEntryManager manager = createCouchbaseEntryManager();

        try {
            List<SimpleClient> attributeList = manager.findEntries("o=gluu", SimpleClient.class, null);
            System.out.println(attributeList);
        } finally {
            manager.destroy();
        }
    }

    @Test(enabled = true) // manual
    public void sampleSessionId() throws IOException, SearchException {
        CouchbaseEntryManager manager = createCouchbaseEntryManager();

        try {
            SessionId sessionId = createSessionId();
            manager.persist(sessionId);

            final String key = "sessions_" + sessionId.getId();
            System.out.println("Key: " + key + ", ttl:" + sessionId.getTtl());

            Bucket sessionBucket = manager.getOperationService().getConnectionProvider().getBucketMapping("sessions").getBucket();
            GetOptions getOptions1 = GetOptions.getOptions().withExpiry(true);
            final GetResult lookup = sessionBucket.defaultCollection().get(key, getOptions1);
            System.out.println("expiry: " + lookup.expiryTime());

            GetOptions getOptions2 = GetOptions.getOptions().project("$document.exptime").withExpiry(true);
            final GetResult ttl = sessionBucket.defaultCollection().get(key, getOptions2);
//            System.out.println("ttl: " + ttl.contentAsObject().get("meta").getLong("$document.exptime"));

            updateSession(sessionId);
            manager.merge(sessionId);

            GetOptions getOptions3 = GetOptions.getOptions().withExpiry(true);
            final GetResult lookup2 = manager.getOperationService().getConnectionProvider().getBucketMapping("sessions").getBucket().defaultCollection().get(key, getOptions3);
            System.out.println("expiry after update: " + lookup2.expiryTime());

        } finally {
            manager.destroy();
        }
    }

    private SessionId createSessionId() {
        SessionId sessionId = new SessionId();
        sessionId.setId(UUID.randomUUID().toString());
        sessionId.setDn(String.format("gluuId=%s,%s", sessionId.getId(), "ou=sessions,o=gluu"));
        sessionId.setCreationDate(new Date());

        updateSession(sessionId);
        return sessionId;
    }

    private void updateSession(SessionId sessionId) {
        final Pair<Date, Integer> expiration = expirationDate(sessionId.getCreationDate());
        sessionId.setLastUsedAt(new Date());
        sessionId.setExpirationDate(expiration.getFirst());
        sessionId.setTtl(expiration.getSecond());
    }

    private Pair<Date, Integer> expirationDate(Date creationDate) {
        int expirationInSeconds = 120;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(creationDate);
        calendar.add(Calendar.SECOND, expirationInSeconds);
        return new Pair<>(calendar.getTime(), expirationInSeconds);
    }

    // MODIFY ACCORDING TO YOUR SERVER
    public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.put("couchbase.auth.userPassword", "secret");

        try (InputStream is = ManualCouchbaseEntryManagerTest.class.getResourceAsStream("cb-bench-backend.jans.io.properties")) {
            properties.load(is);
            return properties;
        }
    }
    private static Properties getSampleConnectionProperties() {
        Properties connectionProperties = new Properties();

        connectionProperties.put("couchbase#servers", "u204.gluu.info");
        connectionProperties.put("couchbase#auth.userName", "admin");
        connectionProperties.put("couchbase#auth.userPassword", "Secret1!");
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
    public static CouchbaseEntryManager createCouchbaseEntryManager() throws IOException {
        CouchbaseEntryManagerFactory couchbaseEntryManagerFactory = new CouchbaseEntryManagerFactory();
        couchbaseEntryManagerFactory.create();

        CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerFactory.createEntryManager(getSampleConnectionProperties());
        System.out.println("Created CouchbaseEntryManager: " + couchbaseEntryManager);

        return couchbaseEntryManager;
    }
}
