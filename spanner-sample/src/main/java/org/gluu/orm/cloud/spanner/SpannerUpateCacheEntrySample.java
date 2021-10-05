/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.cloud.spanner;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.gluu.persist.cloud.spanner.impl.SpannerEntryManager;
import org.gluu.persist.cloud.spanner.model.SimpleCacheEntry;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerConnectionProvider;
import org.gluu.persist.cloud.spanner.persistence.SpannerEntryManagerSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 03/09/2020
 */
public final class SpannerUpateCacheEntrySample {

    private static final Logger LOG = LoggerFactory.getLogger(SpannerConnectionProvider.class);

    private SpannerUpateCacheEntrySample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SpannerEntryManagerSample sqlEntryManagerSample = new SpannerEntryManagerSample();

        // Create SQL entry manager
        SpannerEntryManager sqlEntryManager = sqlEntryManagerSample.createSpannerEntryManager();

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
