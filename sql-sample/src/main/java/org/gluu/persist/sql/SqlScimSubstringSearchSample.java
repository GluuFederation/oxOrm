/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.persist.sql;

import java.util.List;

import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleUser;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class SqlScimSubstringSearchSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlScimSubstringSearchSample.class);

    private SqlScimSubstringSearchSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
    	SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create Couchbase entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();
        Filter filter = Filter.createORFilter(Filter.createORFilter(Filter.createSubstringFilter("oxTrustPhoneValue", null, new String[] {"\"type\":null"}, null).multiValued(), Filter.createSubstringFilter("oxTrustPhoneValue", null, new String[] {"\"value\":\"", "+", "\""}, null).multiValued()),
        		Filter.createSubstringFilter("mail", null, null, "gluu.org"));
        System.out.println(filter);

        List<SimpleUser> users = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter);
        for (SimpleUser user : users) {
            LOG.info("User with uid: '{}' with DN: '{}'", user.getUserId(), user.getDn());
        }
    }

}
