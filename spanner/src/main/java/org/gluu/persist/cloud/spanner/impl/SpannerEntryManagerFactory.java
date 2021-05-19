/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.cloud.spanner.impl;

import java.util.HashMap;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.gluu.persist.PersistenceEntryManager;
import org.gluu.persist.PersistenceEntryManagerFactory;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerConnectionProvider;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerOperationServiceImpl;
import org.gluu.persist.exception.operation.ConfigurationException;
import org.gluu.persist.service.BaseFactoryService;
import org.gluu.orm.util.PropertiesHelper;
import org.gluu.orm.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Couchbase Entry Manager Factory
 *
 * @author Yuriy Movchan Date: 12/18/2020
 */
@ApplicationScoped
public class SpannerEntryManagerFactory implements PersistenceEntryManagerFactory {

    public static final String PERSISTENCE_TYPE = PersistenceEntryManager.PERSITENCE_TYPES.spanner.name();
    public static final String PROPERTIES_FILE = "jans-spanner%s.properties";

	private static final Logger LOG = LoggerFactory.getLogger(SpannerEntryManagerFactory.class);

    @PostConstruct
    public void create() {
    }

    @PreDestroy
    public void destroy() {
    }

    @Override
    public String getPersistenceType() {
        return PERSISTENCE_TYPE;
    }

    @Override
    public HashMap<String, String> getConfigurationFileNames(String alias) {
    	String usedAlias = StringHelper.isEmpty(alias) ? "" : "." + alias; 

    	HashMap<String, String> confs = new HashMap<String, String>();
    	String confFileName = String.format(PROPERTIES_FILE, usedAlias);
    	confs.put(PERSISTENCE_TYPE + usedAlias, confFileName);

    	return confs;
    }

	@Override
    public SpannerEntryManager createEntryManager(Properties conf) {
		Properties entryManagerConf = PropertiesHelper.filterProperties(conf, "#");

		SpannerConnectionProvider connectionProvider = new SpannerConnectionProvider(entryManagerConf);
        connectionProvider.create();
        if (!connectionProvider.isCreated()) {
            throw new ConfigurationException(
                    String.format("Failed to create Spanner connection pool! Result code: '%s'", connectionProvider.getCreationResultCode()));
        }
        LOG.debug("Created connectionProvider '{}' with code '{}'", connectionProvider, connectionProvider.getCreationResultCode());

        
        SpannerEntryManager sqlEntryManager = new SpannerEntryManager(new SpannerOperationServiceImpl(entryManagerConf, connectionProvider));
        LOG.info("Created SpannerEntryManager: {}", sqlEntryManager.getOperationService());

        return sqlEntryManager;
    }

	@Override
	public void initStandalone(BaseFactoryService persistanceFactoryService) {}

}
