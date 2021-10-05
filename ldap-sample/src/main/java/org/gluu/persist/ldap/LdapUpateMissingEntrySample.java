/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.persist.exception.EntryPersistenceException;
import org.gluu.persist.ldap.impl.LdapEntryManager;
import org.gluu.persist.ldap.model.SimpleSessionState;

/**
 * @author Yuriy Movchan Date: 03/09/2020
 */
public final class LdapUpateMissingEntrySample {
    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(LdapUpateMissingEntrySample.class);
    }

    private LdapUpateMissingEntrySample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
    	LdapEntryManagerSample sqlSampleEntryManager = new LdapEntryManagerSample();

        // Create SQL entry manager
        LdapEntryManager sqlEntryManager = sqlSampleEntryManager.createLdapEntryManager();

        String sessionId = UUID.randomUUID().toString();
        final String sessionDn = "uniqueIdentifier=" + sessionId + ",ou=session,o=gluu";

        final SimpleSessionState simpleSessionState = new SimpleSessionState();
        simpleSessionState.setDn(sessionDn);
        simpleSessionState.setId(sessionId);
        simpleSessionState.setLastUsedAt(new Date());

        try {
			sqlEntryManager.merge(simpleSessionState);
			System.out.println("Updated");
		} catch (EntryPersistenceException ex) {
            LOG.info(String.format("Failed to update, root case exception: %s", ex.getCause().getClass()), ex);
		}
    }

}
