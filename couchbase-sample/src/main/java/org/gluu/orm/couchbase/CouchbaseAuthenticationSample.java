/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import java.util.List;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.SimpleUser;
import org.gluu.orm.couchbase.operation.impl.CouchbaseConnectionProvider;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class CouchbaseAuthenticationSample {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

    private CouchbaseAuthenticationSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        CouchbaseEntryManagerSample couchbaseEntryManagerSample = new CouchbaseEntryManagerSample();

        // Create Couchbase entry manager
        CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerSample.createCouchbaseEntryManager();

        List<SimpleUser> users = couchbaseEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, null);
        for (SimpleUser user : users) {
            LOG.info("User with uid: '{}' with DN: '{}'", user.getUserId(), user.getDn());
        }
        
        boolean authenticated = couchbaseEntryManager.authenticate("ou=people,o=gluu", SimpleUser.class, "test_user", "test_user_password");
        LOG.info("User with uid: '{}' with DN: '{}'", "test_user", authenticated);

        Filter userUidFilter = Filter.createEqualityFilter(Filter.createLowercaseFilter("uid"), "test_user");
		List<SimpleUser> foundUsers = couchbaseEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, userUidFilter);
		if (foundUsers.size() > 0) {
			SimpleUser user = foundUsers.get(0);
			LOG.info("Found uid: '{}' with custom attributes: '{}'", user.getUserId(), user.getCustomAttributes());
		}
    }

}
