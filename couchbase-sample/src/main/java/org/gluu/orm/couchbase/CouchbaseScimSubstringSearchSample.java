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
public final class CouchbaseScimSubstringSearchSample {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

    private CouchbaseScimSubstringSearchSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
    	CouchbaseEntryManagerSample couchbaseEntryManagerSample = new CouchbaseEntryManagerSample();

        // Create Couchbase entry manager
        CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerSample.createCouchbaseEntryManager();
        Filter filter = Filter.createORFilter(Filter.createORFilter(Filter.createSubstringFilter("oxTrustPhoneValue", null, new String[] {"\"type\":null"}, null).multiValued(), Filter.createSubstringFilter("oxTrustPhoneValue", null, new String[] {"\"value\":\"", "+", "\""}, null).multiValued()),
        		Filter.createSubstringFilter("mail", null, null, "gluu.org"));
        System.out.println(filter);

        List<SimpleUser> users = couchbaseEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter);
        for (SimpleUser user : users) {
            LOG.info("User with uid: '{}' with DN: '{}'", user.getUserId(), user.getDn());
        }
    }

}
