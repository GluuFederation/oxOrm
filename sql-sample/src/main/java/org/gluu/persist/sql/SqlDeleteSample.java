/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleSession;
import org.gluu.persist.sql.model.SimpleUser;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlDeleteSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlDeleteSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

        // Add 15 sample entries with id
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(new Date());
        expirationDate.add(Calendar.SECOND, -60);

        long startId = System.currentTimeMillis();
        for (int i = 0; i < 15; i++) {
            SimpleSession newSession = new SimpleSession();
            newSession.setDn(String.format("oxId=%s,ou=sessions,o=gluu", System.currentTimeMillis()));
            newSession.setId("" + startId + "_" + i);
            newSession.setDeletable(true);
            newSession.setCreationDate(new Date());
            newSession.setExpirationDate(expirationDate.getTime());
            sqlEntryManager.persist(newSession);

            LOG.info("Added session id: '{}' with DN: '{}'", newSession.getId(), newSession.getDn());
		}
        
        String baseDn = "ou=session,o=gluu";
		Filter filter = Filter.createANDFilter(
		        Filter.createEqualityFilter("del", true),
				Filter.createLessOrEqualFilter("exp", sqlEntryManager.encodeTime(baseDn, new Date()))
        );

		// Dump all session entries
        List<SimpleSession> sessions = sqlEntryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, null);
        for (SimpleSession session : sessions) {
            LOG.info("Found session with id: '{}' with DN: '{}'", session.getId(), session.getDn());
        }

        int result = sqlEntryManager.remove(baseDn, SimpleSession.class, filter, 5);
        LOG.info("Removed '{}' sessions ", result);

        // Dump all session entries after 5 entries removal
        List<SimpleSession> sessions2 = sqlEntryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, null);
        for (SimpleSession session : sessions2) {
            LOG.info("Found session with id: '{}' with DN: '{}'", session.getId(), session.getDn());
        }

        Filter filter2 = Filter.createANDFilter(
		        Filter.createEqualityFilter("del", true),
				Filter.createLessOrEqualFilter("exp", new Date())
        );

        int result2 = sqlEntryManager.remove(baseDn, SimpleSession.class, filter2, 5);
        LOG.info("Removed '{}' sessions ", result2);

        // Dump all session entries after 10 entries removal
        List<SimpleSession> sessions3 = sqlEntryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, null);
        for (SimpleSession session : sessions3) {
            LOG.info("Found session with id: '{}' with DN: '{}'", session.getId(), session.getDn());
        }

        int result3 = sqlEntryManager.remove(baseDn, SimpleSession.class, filter2, 5);
        LOG.info("Removed '{}' sessions ", result3);

        // Dump all session entries after 15 entries removal
        List<SimpleSession> sessions4 = sqlEntryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, null);
        for (SimpleSession session : sessions4) {
            LOG.info("Session with id: '{}' with DN: '{}'", session.getId(), session.getDn());
        }

        int result4 = sqlEntryManager.remove(baseDn, SimpleSession.class, filter2, 5);
        LOG.info("Removed '{}' sessions ", result4);

        // Dump all session entries after next 5 entries removal
        List<SimpleSession> sessions5 = sqlEntryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, null);
        for (SimpleSession session : sessions5) {
            LOG.info("Found session with id: '{}' with DN: '{}'", session.getId(), session.getDn());
        }
    }

}
