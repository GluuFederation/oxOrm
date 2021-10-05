/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Arrays;
import java.util.List;

import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.SortOrder;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleAttribute;
import org.gluu.persist.sql.model.SimpleGrant;
import org.gluu.persist.sql.model.SimpleSession;
import org.gluu.persist.sql.model.SimpleUser;
import org.gluu.persist.sql.model.UmaResource;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlUmaResourceSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlUmaResourceSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

        final Filter filter = Filter.createEqualityFilter("gluuAssociatedClnt", "inum=AB77-1A2B,ou=clients,o=gluu").multiValued();
        List<UmaResource> umaResource = sqlEntryManager.findEntries("ou=resources,ou=uma,o=gluu", UmaResource.class, filter);

        LOG.info("Found umaResources: " + umaResource);
    }

}
