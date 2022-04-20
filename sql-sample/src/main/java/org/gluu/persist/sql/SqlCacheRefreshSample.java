/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.List;

import org.gluu.persist.model.SearchScope;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimleInumMap;
import org.gluu.persist.sql.model.Status;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlCacheRefreshSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlCacheRefreshSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

		String inumbaseDn = "ou=cache-refresh,o=site";

		Filter filterObjectClass = Filter.createEqualityFilter("objectClass",
				"gluuInumMap");
		Filter filterStatus = Filter.createNOTFilter(
				Filter.createEqualityFilter("gluuStatus", Status.INACTIVE.getValue()));
		Filter filter = Filter.createANDFilter(filterObjectClass, filterStatus);

		List<SimleInumMap> result = sqlEntryManager.findEntries(inumbaseDn, SimleInumMap.class, filter, SearchScope.SUB, null,
				null, 0, 0, 1000);
		System.out.println(result);
    }

}
