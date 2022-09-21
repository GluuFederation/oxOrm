/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleGroup;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlCheckGroupSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlCheckGroupSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

		// EQ -- String
		Filter searchFilter = Filter.create("(&(dn=inum=60B7,ou=groups,o=gluu)(|(owner=inum=78c0ea07-d9db-4ccd-9039-4911a6426a0c,ou=people,o=gluu)(member=inum=78c0ea07-d9db-4ccd-9039-4911a6426a0c,ou=people,o=gluu)))");

		boolean isMemberOrOwner = sqlEntryManager.findEntries("inum=60B7,ou=groups,o=gluu", SimpleGroup.class, searchFilter, 1).size() > 0;
		System.out.println(isMemberOrOwner);
    }

}
