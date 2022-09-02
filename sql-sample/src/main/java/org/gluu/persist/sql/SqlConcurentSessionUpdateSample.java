/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleSession;
import org.gluu.persist.sql.model.SimpleSessionState;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlConcurentSessionUpdateSample {

    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(SqlConcurentSessionUpdateSample.class);
    }

    private SqlConcurentSessionUpdateSample() {
    }

    public static void main(String[] args) throws InterruptedException {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        final SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

        try {
            String sessionId = UUID.randomUUID().toString();
            final String sessionDn = "uniqueIdentifier=" + sessionId + ",ou=sessions,o=gluu";
            final String userDn =
                    "inum=@!E8F2.853B.1E7B.ACE2!0001!39A4.C163!0000!A8F2.DE1E.D7FB,ou=people,o=gluu";

            Calendar expirationDate = Calendar.getInstance();
            expirationDate.setTime(new Date());
            expirationDate.add(Calendar.SECOND, 60);

            SimpleSession newSession = new SimpleSession();

            newSession.setDn(String.format("oxId=%s,ou=sessions,o=gluu", sessionId));
            newSession.setId(sessionId);
            newSession.setDeletable(true);
            newSession.setCreationDate(new Date());
            newSession.setExpirationDate(expirationDate.getTime());
            newSession.setLastUsedAt(new Date());
            newSession.setUserDn(userDn);

            sqlEntryManager.persist(newSession);
            System.out.println("Persisted");

            int threadCount = 500;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount, daemonThreadFactory());
            for (int i = 0; i < threadCount; i++) {
                final int count = i;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        final SimpleSession simpleSessionFromSQL = sqlEntryManager.find(SimpleSession.class, sessionDn);
                        String beforeUserDn = simpleSessionFromSQL.getUserDn();
                        String randomUserDn = count % 2 == 0 ? userDn : "";

                        try {
                        	simpleSessionFromSQL.setUserDn(randomUserDn);
                        	simpleSessionFromSQL.setLastUsedAt(new Date());
                            sqlEntryManager.merge(simpleSessionFromSQL);
                            System.out.println("Merged thread: " + count + ", userDn: " + randomUserDn + ", before userDn: " + beforeUserDn);
                        } catch (Throwable e) {
                            System.err.println("ERROR !!!, thread: " + count + ", userDn: " + randomUserDn + ", before userDn: " + beforeUserDn
                                    + ", error:" + e.getMessage());
                            // e.printStackTrace();
                        }
                    }
                });
            }

            Thread.sleep(5000L);
        } finally {
            sqlEntryManager.destroy();
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
