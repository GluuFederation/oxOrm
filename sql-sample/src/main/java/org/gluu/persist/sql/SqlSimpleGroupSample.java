/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.List;

import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleGroup;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 05/26/2021
 */
public final class SqlSimpleGroupSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlSimpleGroupSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();
        
        String personDN = "inum=ae8c7ff9-def7-4b42-8147-de8380617a37,ou=people,o=gluu";
		Filter ownerFilter = Filter.createEqualityFilter("owner", personDN);
		Filter memberFilter = Filter.createEqualityFilter("member", personDN);
		Filter searchFilter = Filter.createORFilter(ownerFilter, memberFilter);

		List<SimpleGroup> result = sqlEntryManager.findEntries("ou=groups,o=gluu", SimpleGroup.class, searchFilter, 1);
		System.out.println(result);
    }

}
