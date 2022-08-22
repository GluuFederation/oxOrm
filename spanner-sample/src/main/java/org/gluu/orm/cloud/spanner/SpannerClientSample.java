/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package org.gluu.orm.cloud.spanner;

import java.util.List;

import org.gluu.persist.cloud.spanner.impl.SpannerEntryManager;
import org.gluu.persist.cloud.spanner.model.SimpleClient;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerConnectionProvider;
import org.gluu.persist.cloud.spanner.persistence.SpannerEntryManagerSample;
import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SpannerClientSample {

    private static final Logger LOG = LoggerFactory.getLogger(SpannerConnectionProvider.class);

    private SpannerClientSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SpannerEntryManagerSample sqlEntryManagerSample = new SpannerEntryManagerSample();

        // Create SQL entry manager
        SpannerEntryManager sqlEntryManager = sqlEntryManagerSample.createSpannerEntryManager();

        List<SimpleClient> result1 = sqlEntryManager.findEntries("o=gluu", SimpleClient.class, null, SearchScope.SUB,
                null, null, 0, 0, 1000);
        
        LOG.info("Found clients: " + result1.size());

        PagedResult<SimpleClient> result2 = sqlEntryManager.findPagedEntries("o=gluu", SimpleClient.class, null, null,
                null, null, 0, 0, 1000);

        LOG.info("Found clients: " + result2.getEntriesCount());

        
        LOG.info("Result count (without collecting results): " + result1.size());
    }

}