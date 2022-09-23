/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.List;

import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleUser;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlScimUserSearchSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlScimUserSearchSample() {
    }

    public static void main(String[] args) throws InterruptedException {
        // Prepare sample connection details
    	final SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();
        final SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();
        
        Filter filter0 = Filter.createEqualityFilter(Filter.createLowercaseFilter("uid"),"test-0.8372945581513689");
        System.out.println(filter0);
        List<SimpleUser> users0 = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter0);
        System.out.println(users0);

        Filter filter1 = Filter.createORFilter(Filter.createSubstringFilter("oxTrustImsValue",null, new String[] {"\"value\":\"Skype\""}, null).multiValued(),
        		Filter.createEqualityFilter("nickname", null), Filter.createSubstringFilter("nickname", null, new String[] {}, null));
        System.out.println(filter1);
        List<SimpleUser> users = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter1);
        System.out.println(users);

        Filter filter2 = Filter.createGreaterOrEqualFilter("oxCreationTimestamp","2022-09-23T09:01:28.637");
        System.out.println(filter2);
        List<SimpleUser> users2 = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter2);
        System.out.println(users2);
    }

}
