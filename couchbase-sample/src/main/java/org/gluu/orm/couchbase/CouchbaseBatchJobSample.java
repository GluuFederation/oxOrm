/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.SimpleCache;
import org.gluu.orm.couchbase.model.SimpleClient;
import org.gluu.orm.couchbase.model.SimpleSession;
import org.gluu.orm.couchbase.model.SimpleToken;
import org.gluu.persist.exception.EntryPersistenceException;
import org.gluu.persist.model.BatchOperation;
import org.gluu.persist.model.DefaultBatchOperation;
import org.gluu.persist.model.ProcessBatchOperation;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.base.CustomAttribute;
import org.gluu.search.filter.Filter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eugeniuparvan on 1/12/17.
 */
public final class CouchbaseBatchJobSample {
    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(CouchbaseSample.class);
    }

    private CouchbaseBatchJobSample() { }

    public static void main(String[] args) {
        // Prepare sample connection details
        CouchbaseEntryManagerSample couchbaseEntryManagerSample = new CouchbaseEntryManagerSample();

        // Create Couchbase entry manager
        final CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerSample.createCouchbaseEntryManager();

        BatchOperation<SimpleToken> tokenCouchbaseBatchOperation = new ProcessBatchOperation<SimpleToken>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleToken> objects) {
                for (SimpleToken simpleTokenCouchbase : objects) {
                    try {
                        CustomAttribute customAttribute = getUpdatedAttribute(couchbaseEntryManager, simpleTokenCouchbase.getDn(), "exp",
                                simpleTokenCouchbase.getAttribute("exp"));
                        simpleTokenCouchbase.setCustomAttributes(Arrays.asList(new CustomAttribute[] {customAttribute}));
                        couchbaseEntryManager.merge(simpleTokenCouchbase);
                        processedCount++;
                    } catch (EntryPersistenceException ex) {
                        LOG.error("Failed to update entry", ex);
                    }
                } 

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter1 = Filter.createPresenceFilter("exp");
        couchbaseEntryManager.findEntries("ou=tokens,o=gluu", SimpleToken.class, filter1, SearchScope.SUB, new String[] {"exp"},
                tokenCouchbaseBatchOperation, 0, 0, 100);

        BatchOperation<SimpleSession> sessionBatchOperation = new ProcessBatchOperation<SimpleSession>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleSession> objects) {
                for (SimpleSession simpleSession : objects) {
                    try {
                        CustomAttribute customAttribute = getUpdatedAttribute(couchbaseEntryManager, simpleSession.getDn(), "gluuLastAccessTime",
                                simpleSession.getAttribute("gluuLastAccessTime"));
                        simpleSession.setCustomAttributes(Arrays.asList(new CustomAttribute[] {customAttribute}));
                        couchbaseEntryManager.merge(simpleSession);
                        processedCount++;
                    } catch (EntryPersistenceException ex) {
                        LOG.error("Failed to update entry", ex);
                    }
                }

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter2 = Filter.createPresenceFilter("gluuLastAccessTime");
        couchbaseEntryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, filter2, SearchScope.SUB, new String[] {"gluuLastAccessTime"},
                sessionBatchOperation, 0, 0, 100);

        if (false) {
            Calendar calendar = Calendar.getInstance();
            Date jansLastAccessTimeDate = new Date();
            calendar.setTime(jansLastAccessTimeDate);
            calendar.add(Calendar.SECOND, 60);
            Date date = calendar.getTime();

            for (int i = 0; i < 1111; i++) {
	            String id = String.format("cache_%06d", i);
	            String dn = String.format("id=%s,ou=cache,o=gluu", id);
	
	            SimpleCache newCache = new SimpleCache();
	            newCache.setDn(dn);
	            newCache.setId(id);
	            newCache.setData("{'sample_data': 'sample_data_value'}");
	            newCache.setExpirationDate(date);
	            newCache.setDeletable(true);
	
	    		try {
	                couchbaseEntryManager.persist(newCache);
	            } catch (Throwable e) {
	                e.printStackTrace();
	            }
	        }
        }

        BatchOperation<SimpleCache> clientBatchOperation = new ProcessBatchOperation<SimpleCache>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleCache> objects) {
                for (SimpleCache simpleCache : objects) {
                    processedCount++;
                }

                LOG.info("Total processed: " + processedCount);
            }
        };
        final Filter filter3 = Filter.createPresenceFilter("exp");
        List<SimpleCache> result3 = couchbaseEntryManager.findEntries("ou=cache,o=gluu", SimpleCache.class, filter3, SearchScope.SUB,
                new String[] {"exp"}, clientBatchOperation, 0, 0, 333);

        LOG.info("Result count (without collecting results): " + result3.size());

        BatchOperation<SimpleCache> clientBatchOperation2 = new DefaultBatchOperation<SimpleCache>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleCache> objects) {
                for (SimpleCache simpleCache : objects) {
                    processedCount++;
                }

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter4 = Filter.createPresenceFilter("exp");
        List<SimpleCache> result4 = couchbaseEntryManager.findEntries("ou=cache,o=gluu", SimpleCache.class, filter4, SearchScope.SUB,
                new String[] {"exp"}, clientBatchOperation2, 0, 0, 333);

        LOG.info("Result count (with collecting results): " + result4.size());
    }

    private static CustomAttribute getUpdatedAttribute(CouchbaseEntryManager couchbaseEntryManager, String baseDn, String attributeName, String attributeValue) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date jansLastAccessTimeDate = new Date();
            calendar.setTime(jansLastAccessTimeDate);
            calendar.add(Calendar.SECOND, -1);

            CustomAttribute customAttribute = new CustomAttribute();
            customAttribute.setName(attributeName);
            customAttribute.setValue(couchbaseEntryManager.encodeTime(baseDn, calendar.getTime()));
            return customAttribute;
        } catch (Exception ex) {
            LOG.error("Can't parse attribute", ex);
        }
        return null;
    }
}
