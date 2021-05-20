/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.persist.ldap.impl.LdapEntryManager;
import org.gluu.persist.ldap.model.SimpleSessionState;

/**
 * @author Yuriy Movchan Date: 01/25/2016
 */
public final class LdapSimpleSessionSample {

    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(LdapSimpleSessionSample.class);
    }

    private LdapSimpleSessionSample() {
    }

    public static void main(String[] args) throws InterruptedException {
        // Prepare sample connection details
        LdapEntryManagerSample ldapEntryManagerSample = new LdapEntryManagerSample();
        final LdapEntryManager ldapEntryManager = ldapEntryManagerSample.createLdapEntryManager();

        try {

            // Create LDAP entry manager
            String sessionId = "xyzcyzxy-a41a-45ad-8a83-61485dbad561";
            final String sessionDn = "uniqueIdentifier=" + sessionId + ",ou=session,o=gluu";
            final String userDn =
                    "inum=@!E8F2.853B.1E7B.ACE2!0001!39A4.C163!0000!A8F2.DE1E.D7FB,ou=people,o=gluu";

            final SimpleSessionState simpleSessionState = new SimpleSessionState();
            simpleSessionState.setDn(sessionDn);
            simpleSessionState.setId(sessionId);
            simpleSessionState.setLastUsedAt(new Date());

            ldapEntryManager.persist(simpleSessionState);
            System.out.println("Persisted");

            int threadCount = 500;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount, daemonThreadFactory());
            for (int i = 0; i < threadCount; i++) {
                final int count = i;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        final SimpleSessionState simpleSessionStateFromLdap = ldapEntryManager.find(SimpleSessionState.class, sessionDn);
                        String beforeUserDn = simpleSessionStateFromLdap.getUserDn();
                        String randomUserDn = count % 2 == 0 ? userDn : "";

                        try {
                            simpleSessionStateFromLdap.setUserDn(randomUserDn);
                            simpleSessionStateFromLdap.setLastUsedAt(new Date());
                            ldapEntryManager.merge(simpleSessionStateFromLdap);
                            System.out.println("Merged thread: " + count + ", userDn: " + randomUserDn + ", before userDn: " + beforeUserDn);
                        } catch (Throwable e) {
                            System.out.println("ERROR !!!, thread: " + count + ", userDn: " + randomUserDn + ", before userDn: " + beforeUserDn
                                    + ", error:" + e.getMessage());
                            // e.printStackTrace();
                        }
                    }
                });
            }

            Thread.sleep(5000L);
        } finally {
            ldapEntryManager.getOperationService().getConnectionPool().close();
        }
    }

    public static ThreadFactory daemonThreadFactory() {
        return new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        };
    }

}
