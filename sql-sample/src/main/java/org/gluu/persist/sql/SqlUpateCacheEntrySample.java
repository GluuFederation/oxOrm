/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleCacheEntry;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 03/09/2020
 */
public final class SqlUpateCacheEntrySample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlUpateCacheEntrySample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

        String key = UUID.randomUUID().toString();
        final String cacheDn = String.format("uuid=%s,%s", key, "ou=cache,o=gluu");

        int expirationInSeconds = 60;
        Calendar expirationDate = Calendar.getInstance();
		expirationDate.setTime(new Date());
		expirationDate.add(Calendar.SECOND, expirationInSeconds);

        SimpleCacheEntry entity = new SimpleCacheEntry();
        entity.setTtl(expirationInSeconds);
        entity.setData("sample_data");
        entity.setId(key);
        entity.setDn(cacheDn);
        entity.setCreationDate(new Date());
        entity.setExpirationDate(expirationDate.getTime());
        entity.setDeletable(true);

		sqlEntryManager.persist(entity);

		// Try to update
		sqlEntryManager.merge(entity);

		// Try to update with removed value
        entity.setData(null);
		sqlEntryManager.merge(entity);
    }

}
