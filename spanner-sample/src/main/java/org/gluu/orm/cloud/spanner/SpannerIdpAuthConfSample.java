/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.cloud.spanner;

import org.gluu.persist.cloud.spanner.impl.SpannerEntryManager;
import org.gluu.persist.cloud.spanner.model.JansConfiguration;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerConnectionProvider;
import org.gluu.persist.cloud.spanner.persistence.SpannerEntryManagerSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SpannerIdpAuthConfSample {

    private static final Logger LOG = LoggerFactory.getLogger(SpannerConnectionProvider.class);

    private SpannerIdpAuthConfSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SpannerEntryManagerSample sqlEntryManagerSample = new SpannerEntryManagerSample();

        // Create SQL entry manager
        SpannerEntryManager sqlEntryManager = sqlEntryManagerSample.createSpannerEntryManager();

        JansConfiguration jansConfiguration = sqlEntryManager.find(JansConfiguration.class, "ou=configuration,o=jans");

        LOG.info("Found jansConfiguration: " + jansConfiguration);
    }

}
