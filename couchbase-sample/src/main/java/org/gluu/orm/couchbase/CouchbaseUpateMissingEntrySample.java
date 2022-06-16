/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.SimpleSessionState;
import org.gluu.orm.couchbase.operation.impl.CouchbaseConnectionProvider;
import org.gluu.persist.exception.EntryPersistenceException;

/**
 * @author Yuriy Movchan Date: 03/09/2020
 */
public final class CouchbaseUpateMissingEntrySample {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

    private CouchbaseUpateMissingEntrySample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
    	CouchbaseEntryManagerSample sqlSampleEntryManager = new CouchbaseEntryManagerSample();

        // Create SQL entry manager
        CouchbaseEntryManager sqlEntryManager = sqlSampleEntryManager.createCouchbaseEntryManager();

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
            LOG.info("Failed to update, root case exception: {}", ex.getCause().getClass(), ex);
            LOG.info("Test Passed :) It's right behaviour.");
		}
    }

}
