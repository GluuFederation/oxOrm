/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.cloud.spanner.persistence;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.gluu.persist.cloud.spanner.impl.SpannerEntryManager;
import org.gluu.persist.cloud.spanner.impl.SpannerEntryManagerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public class SpannerEntryManagerSample {

    private static final Logger LOG = Logger.getLogger(SpannerEntryManagerSample.class);

    private Properties getSampleConnectionProperties() {
        Properties connectionProperties = new Properties();

        connectionProperties.put("spanner#connection.project", "gluu-project");
        connectionProperties.put("spanner#connection.instance", "gluu-instance");
        connectionProperties.put("spanner#connection.database", "gluudb");

        connectionProperties.put("spanner#connection.emulator-host", "jenkins-dev1.gluu.org:9010");
        
        // Password hash method
        connectionProperties.put("spanner#password.encryption.method", "SSHA-256");
        
        // Max time needed to create connection pool in milliseconds
        connectionProperties.put("spanner#connection.pool.create-max-wait-time-millis", "20000");

        // # Maximum allowed statement result set size
        connectionProperties.put("spanner#statement.limit.default-maximum-result-size", "1000");

        // # Maximum allowed delete statement result set size
        connectionProperties.put("spanner#statement.limit.maximum-result-delete-size", "10000");

        connectionProperties.put("spanner#binaryAttributes", "objectGUID");
        connectionProperties.put("spanner#certificateAttributes", "userCertificate");

        return connectionProperties;
    }

    public SpannerEntryManager createSpannerEntryManager() {
        SpannerEntryManagerFactory sqlEntryManagerFactory = new SpannerEntryManagerFactory();
        sqlEntryManagerFactory.create();
        Properties connectionProperties = getSampleConnectionProperties();

        SpannerEntryManager sqlEntryManager = sqlEntryManagerFactory.createEntryManager(connectionProperties);
        LOG.debug("Created SpannerEntryManager: " + sqlEntryManager);

        return sqlEntryManager;
    }

}