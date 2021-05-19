/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap;

import com.unboundid.util.StaticUtils;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.orm.search.filter.Filter;
import org.gluu.persist.exception.EntryPersistenceException;
import org.gluu.persist.ldap.impl.LdapEntryManager;
import org.gluu.persist.ldap.model.SimpleClient;
import org.gluu.persist.ldap.model.SimpleSession;
import org.gluu.persist.ldap.model.SimpleTokenLdap;
import org.gluu.persist.model.BatchOperation;
import org.gluu.persist.model.DefaultBatchOperation;
import org.gluu.persist.model.ProcessBatchOperation;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.base.CustomAttribute;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eugeniuparvan on 1/12/17.
 */
public final class LdapBatchJobSample {
    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(LdapSample.class);
    }

    private LdapBatchJobSample() { }

    public static void main(String[] args) {
        // Prepare sample connection details
        LdapEntryManagerSample ldapEntryManagerSample = new LdapEntryManagerSample();

        // Create LDAP entry manager
        final LdapEntryManager ldapEntryManager = ldapEntryManagerSample.createLdapEntryManager();

        BatchOperation<SimpleTokenLdap> tokenLdapBatchOperation = new ProcessBatchOperation<SimpleTokenLdap>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleTokenLdap> objects) {
                for (SimpleTokenLdap simpleTokenLdap : objects) {
                    try {
                        CustomAttribute customAttribute = getUpdatedAttribute(ldapEntryManager, simpleTokenLdap.getDn(), "exp",
                                simpleTokenLdap.getAttribute("exp"));
                        simpleTokenLdap.setCustomAttributes(Arrays.asList(new CustomAttribute[] {customAttribute}));
                        ldapEntryManager.merge(simpleTokenLdap);
                        processedCount++;
                    } catch (EntryPersistenceException ex) {
                        LOG.error("Failed to update entry", ex);
                    }
                }

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter1 = Filter.createPresenceFilter("exp");
        ldapEntryManager.findEntries("o=jans", SimpleTokenLdap.class, filter1, SearchScope.SUB, new String[] {"exp"},
                tokenLdapBatchOperation, 0, 0, 100);

        BatchOperation<SimpleSession> sessionBatchOperation = new ProcessBatchOperation<SimpleSession>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleSession> objects) {
                for (SimpleSession simpleSession : objects) {
                    try {
                        CustomAttribute customAttribute = getUpdatedAttribute(ldapEntryManager, simpleSession.getDn(), "jansLastAccessTime",
                                simpleSession.getAttribute("jansLastAccessTime"));
                        simpleSession.setCustomAttributes(Arrays.asList(new CustomAttribute[] {customAttribute}));
                        ldapEntryManager.merge(simpleSession);
                        processedCount++;
                    } catch (EntryPersistenceException ex) {
                        LOG.error("Failed to update entry", ex);
                    }
                }

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter2 = Filter.createPresenceFilter("jansLastAccessTime");
        ldapEntryManager.findEntries("o=jans", SimpleSession.class, filter2, SearchScope.SUB, new String[] {"jansLastAccessTime"},
                sessionBatchOperation, 0, 0, 100);

        BatchOperation<SimpleClient> clientBatchOperation = new ProcessBatchOperation<SimpleClient>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleClient> objects) {
                for (SimpleClient simpleClient : objects) {
                    processedCount++;
                }

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter3 = Filter.createPresenceFilter("exp");
        List<SimpleClient> result3 = ldapEntryManager.findEntries("o=jans", SimpleClient.class, filter3, SearchScope.SUB,
                new String[] {"exp"}, clientBatchOperation, 0, 0, 1000);

        LOG.info("Result count (without collecting results): " + result3.size());

        BatchOperation<SimpleClient> clientBatchOperation2 = new DefaultBatchOperation<SimpleClient>() {
            private int processedCount = 0;

            @Override
            public void performAction(List<SimpleClient> objects) {
                for (SimpleClient simpleClient : objects) {
                    processedCount++;
                }

                LOG.info("Total processed: " + processedCount);
            }
        };

        final Filter filter4 = Filter.createPresenceFilter("exp");
        List<SimpleClient> result4 = ldapEntryManager.findEntries("o=jans", SimpleClient.class, filter4, SearchScope.SUB,
                new String[] {"exp"}, clientBatchOperation2, 0, 0, 1000);

        LOG.info("Result count (with collecting results): " + result4.size());
    }

    private static CustomAttribute getUpdatedAttribute(LdapEntryManager ldapEntryManager, String baseDn, String attributeName, String attributeValue) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date jansLastAccessTimeDate = StaticUtils.decodeGeneralizedTime(attributeValue);
            calendar.setTime(jansLastAccessTimeDate);
            calendar.add(Calendar.SECOND, -1);

            CustomAttribute customAttribute = new CustomAttribute();
            customAttribute.setName(attributeName);
            customAttribute.setValue(ldapEntryManager.encodeTime(baseDn, calendar.getTime()));
            return customAttribute;
        } catch (ParseException e) {
            LOG.error("Can't parse attribute", e);
        }
        return null;
    }
}
