/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Arrays;
import java.util.List;

import org.gluu.orm.util.ArrayHelper;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleClient;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 05/26/2021
 */
public final class SqlSimpleClientSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlSimpleClientSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();
        
		SimpleClient newClient = new SimpleClient();
		newClient.setDn("inum=test_acr2,ou=client,o=gluu");
		newClient.setDefaultAcrValues(new String[] {"test_acr2"});
		newClient.setClientName("test_acr2");
		
		sqlEntryManager.persist(newClient);

		Filter presenceFilter = Filter.createEqualityFilter("displayName", "test_acr2");
		List<SimpleClient> results = sqlEntryManager.findEntries("ou=client,o=gluu", SimpleClient.class, presenceFilter);
		for (SimpleClient client : results) {
			String[] acrs = client.getDefaultAcrValues();
			if (ArrayHelper.isNotEmpty(acrs)) {
				System.out.println(Arrays.toString(acrs));
			}
		}
		
		sqlEntryManager.remove(newClient.getDn(), SimpleClient.class);
    }

}
