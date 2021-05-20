/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.SimpleUser;
import org.gluu.orm.couchbase.operation.impl.CouchbaseConnectionProvider;
import org.gluu.persist.model.base.CustomAttribute;
import org.gluu.persist.model.base.CustomEntry;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class CouchbaseUpdateAttributeSample {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

    private CouchbaseUpdateAttributeSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        CouchbaseEntryManagerSample couchbaseEntryManagerSample = new CouchbaseEntryManagerSample();

        // Create Couchbase entry manager
        CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerSample.createCouchbaseEntryManager();

        String uid = "sample_user_" + System.currentTimeMillis();
        String dn = String.format("inum=%s,ou=people,o=gluu", System.currentTimeMillis());

        SimpleUser newUser = new SimpleUser();
        newUser.setDn(dn);
        newUser.setUserId(uid);
        newUser.setUserPassword("test");
        couchbaseEntryManager.persist(newUser);

        SimpleUser user = couchbaseEntryManager.find(SimpleUser.class, dn);
        LOG.info("Found user '{}'", user);

        CustomEntry customEntry = new CustomEntry();
		customEntry.setDn(user.getDn());
		customEntry.setCustomObjectClasses(new String[] { "gluuPerson" });

		Date now = new GregorianCalendar(TimeZone.getTimeZone("UTC")).getTime();
		String nowDateString = couchbaseEntryManager.encodeTime(customEntry.getDn(), now);
		CustomAttribute customAttribute = new CustomAttribute("gluuLastLogonTime", nowDateString);
		customEntry.getCustomAttributes().add(customAttribute);

		couchbaseEntryManager.merge(customEntry);

        SimpleUser userAfterUpdate = couchbaseEntryManager.find(SimpleUser.class, dn);
        LOG.info("Found user after update '{}'", userAfterUpdate);
    }

}
