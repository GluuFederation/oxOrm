/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.persist.exception.EntryPersistenceException;
import org.gluu.persist.model.BatchOperation;
import org.gluu.persist.model.DefaultBatchOperation;
import org.gluu.persist.model.ProcessBatchOperation;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.base.CustomAttribute;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleClient;
import org.gluu.persist.sql.model.SimpleSession;
import org.gluu.persist.sql.model.SimpleToken;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlBatchJobSample {
    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(SqlSample.class);
    }

    private SqlBatchJobSample() { }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        final SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

        BatchOperation<SimpleToken> tokenSQLBatchOperation = new ProcessBatchOperation<SimpleToken>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleToken> objects) {
                for (SimpleToken simpleTokenSQL : objects) {
                    try {
                        CustomAttribute customAttribute = getUpdatedAttribute(sqlEntryManager, simpleTokenSQL.getDn(), "exp",
                                simpleTokenSQL.getAttribute("exp"));
                        simpleTokenSQL.setCustomAttributes(Arrays.asList(new CustomAttribute[] {customAttribute}));
                        sqlEntryManager.merge(simpleTokenSQL);
                        processedCount++;
                    } catch (EntryPersistenceException ex) {
                        LOG.error("Failed to update entry", ex);
                    }
                }

                LOG.info("Total processed tokens: " + processedCount);
            }
        };

        final Filter filter1 = Filter.createPresenceFilter("exp");
        sqlEntryManager.findEntries("o=gluu", SimpleToken.class, filter1, SearchScope.SUB, new String[] {"exp"},
                tokenSQLBatchOperation, 0, 0, 100);

        BatchOperation<SimpleSession> sessionBatchOperation = new ProcessBatchOperation<SimpleSession>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleSession> objects) {
            	int currentProcessedCount = 0;
                for (SimpleSession simpleSession : objects) {
                    try {
                        CustomAttribute customAttribute = getUpdatedAttribute(sqlEntryManager, simpleSession.getDn(), "gluuLastAccessTime",
                                simpleSession.getAttribute("gluuLastAccessTime"));
                        simpleSession.setCustomAttributes(Arrays.asList(new CustomAttribute[] {customAttribute}));
                        sqlEntryManager.merge(simpleSession);
                        processedCount++;
                        currentProcessedCount++;
                    } catch (EntryPersistenceException ex) {
                        LOG.error("Failed to update entry", ex);
                    }
                }
                LOG.info("Currnet batch count processed sessions: " + currentProcessedCount);

                LOG.info("Total processed sessions: " + processedCount);
            }
        };

        final Filter filter2 = Filter.createPresenceFilter("gluuLastAccessTime");
        sqlEntryManager.findEntries("o=gluu", SimpleSession.class, filter2, SearchScope.SUB, new String[] {"gluuLastAccessTime"},
                sessionBatchOperation, 0, 0, 100);

        BatchOperation<SimpleClient> clientBatchOperation = new ProcessBatchOperation<SimpleClient>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleClient> objects) {
                for (SimpleClient simpleClient : objects) {
                    processedCount++;
                }

                LOG.info("Total processed clients: " + processedCount);
            }
        };

        final Filter filter3 = Filter.createPresenceFilter("exp");
        List<SimpleClient> result3 = sqlEntryManager.findEntries("o=gluu", SimpleClient.class, filter3, SearchScope.SUB,
                new String[] {"exp"}, clientBatchOperation, 0, 0, 1000);

        LOG.info("Result count (without collecting results): " + result3.size());

        BatchOperation<SimpleClient> clientBatchOperation2 = new DefaultBatchOperation<SimpleClient>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleClient> objects) {
                for (SimpleClient simpleClient : objects) {
                    processedCount++;
                }

                LOG.info("Total processed clients: " + processedCount);
            }
        };

        final Filter filter4 = Filter.createPresenceFilter("exp");
        List<SimpleClient> result4 = sqlEntryManager.findEntries("o=gluu", SimpleClient.class, filter4, SearchScope.SUB,
                new String[] {"exp"}, clientBatchOperation2, 0, 0, 1000);

        LOG.info("Result count (with collecting results): " + result4.size());
    }

    private static CustomAttribute getUpdatedAttribute(SqlEntryManager sqlEntryManager, String baseDn, String attributeName, String attributeValue) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date jansLastAccessTimeDate = new Date();
            calendar.setTime(jansLastAccessTimeDate);
            calendar.add(Calendar.SECOND, -1);

            CustomAttribute customAttribute = new CustomAttribute();
            customAttribute.setName(attributeName);
            customAttribute.setValue(sqlEntryManager.encodeTime(baseDn, calendar.getTime()));
            return customAttribute;
        } catch (Exception ex) {
            LOG.error("Can't parse attribute", ex);
        }
        return null;
    }
}
