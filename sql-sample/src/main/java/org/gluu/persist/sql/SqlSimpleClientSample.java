/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        String uuid = UUID.randomUUID().toString();
        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();
        
		SimpleClient newClient = new SimpleClient();
		newClient.setDn(String.format("inum=test_acr2_%s,ou=client,o=gluu", uuid));
		newClient.setDefaultAcrValues(new String[] {"test_acr2_" + uuid});
		newClient.setClientName("test_acr2_" + uuid);
		
		sqlEntryManager.persist(newClient);

		Filter presenceFilter = Filter.createSubstringFilter("displayName", null, new String[] { "test_acr2" }, null);
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
