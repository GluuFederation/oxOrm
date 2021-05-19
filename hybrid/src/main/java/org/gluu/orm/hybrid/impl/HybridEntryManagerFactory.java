/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.hybrid.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gluu.persist.PersistenceEntryManager;
import org.gluu.persist.PersistenceEntryManagerFactory;
import org.gluu.persist.exception.operation.ConfigurationException;
import org.gluu.persist.operation.PersistenceOperationService;
import org.gluu.persist.service.BaseFactoryService;
import org.gluu.orm.util.PropertiesHelper;
import org.gluu.orm.util.StringHelper;
import org.gluu.orm.util.properties.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hybrid Entry Manager Factory
 *
 * @author Yuriy Movchan Date: 05/13/2018
 */
@ApplicationScoped
public class HybridEntryManagerFactory implements PersistenceEntryManagerFactory {
	static {
		if (System.getProperty("jans.base") != null) {
			BASE_DIR = System.getProperty("jans.base");
		} else if ((System.getProperty("catalina.base") != null) && (System.getProperty("catalina.base.ignore") == null)) {
			BASE_DIR = System.getProperty("catalina.base");
		} else if (System.getProperty("catalina.home") != null) {
			BASE_DIR = System.getProperty("catalina.home");
		} else if (System.getProperty("jboss.home.dir") != null) {
			BASE_DIR = System.getProperty("jboss.home.dir");
		} else {
			BASE_DIR = null;
		}
	}

	public static final String BASE_DIR;
	public static final String DIR = BASE_DIR + File.separator + "conf" + File.separator;

    public static final String PERSISTENCE_TYPE = PersistenceEntryManager.PERSITENCE_TYPES.hybrid.name();
    public static final String PROPERTIES_FILE = "jans-hybrid%s.properties";

	private static final Logger LOG = LoggerFactory.getLogger(HybridEntryManagerFactory.class);
	
	@Inject
	private BaseFactoryService persistanceFactoryService;

	private String[] persistenceTypes;

	private Properties hybridMappingProperties;

    @Override
    public String getPersistenceType() {
        return PERSISTENCE_TYPE;
    }

    @Override
    public HashMap<String, String> getConfigurationFileNames(String alias) {
    	String usedAlias = StringHelper.isEmpty(alias) ? "" : "-" + alias; 

    	HashMap<String, String> confs = new HashMap<String, String>();
    	String confFileName = String.format(PROPERTIES_FILE, usedAlias);
    	confs.put(PERSISTENCE_TYPE + usedAlias, confFileName);

    	HashMap<String, String> allConfs = getAllConfigurationFileNames(alias, confFileName);
    	confs.putAll(allConfs);

    	return confs;
    }

    private HashMap<String, String> getAllConfigurationFileNames(String alias, String confFileName) {
    	HashMap<String, String> allConfs = new HashMap<String, String>();

		FileConfiguration fileConf = new FileConfiguration(DIR + confFileName);
		if (!fileConf.isLoaded()) {
			LOG.error("Unable to load configuration file '{}'", DIR + confFileName);
            throw new ConfigurationException(String.format("Unable to load configuration file: '%s'", fileConf));
		}

		String storagesList = fileConf.getString("storages", null);
		if (StringHelper.isEmpty(storagesList)) {
            throw new ConfigurationException("'storages' key not exists or value is empty!");
		}
		
		this.persistenceTypes = StringHelper.split(storagesList, ",");
		for (String persistenceType : persistenceTypes) {
			PersistenceEntryManagerFactory persistenceEntryManagerFactory = persistanceFactoryService.getPersistenceEntryManagerFactory(persistenceType);
			if (persistenceEntryManagerFactory == null) {
				throw new ConfigurationException(String.format("Unable to get Persistence Entry Manager Factory by type '%s'", persistenceType));
			}
			
			String persistenceTypeAlias = persistanceFactoryService.getPersistenceTypeAlias(persistenceType);
	    	Map<String, String> confs = persistenceEntryManagerFactory.getConfigurationFileNames(persistenceTypeAlias);
	    	allConfs.putAll(confs);
		}

		return allConfs;
	}

    @Override
    public HybridEntryManager createEntryManager(Properties conf) {
    	HashMap<String, PersistenceEntryManager> persistenceEntryManagers = new HashMap<String, PersistenceEntryManager>();
    	List<PersistenceOperationService> operationServices = new ArrayList<PersistenceOperationService>();

    	if (persistenceTypes == null) {
    		Properties hybridProperties = PropertiesHelper.findProperties(conf, PERSISTENCE_TYPE, "#");
    		hybridProperties = PropertiesHelper.filterProperties(hybridProperties, "#");

    		String storagesList = hybridProperties.getProperty("storages", null);
    		if (StringHelper.isEmpty(storagesList)) {
                throw new ConfigurationException("'storages' key not exists or value is empty!");
    		}
    		this.persistenceTypes = StringHelper.split(storagesList, ",");
    	}

    	for (String persistenceType : persistenceTypes) {
			PersistenceEntryManagerFactory persistenceEntryManagerFactory = persistanceFactoryService.getPersistenceEntryManagerFactory(persistenceType);
			if (persistenceEntryManagerFactory == null) {
				throw new ConfigurationException(String.format("Unable to get Persistence Entry Manager Factory by type '%s'", persistenceType));
			}

			Properties entryManagerConf = PropertiesHelper.findProperties(conf, persistenceType, "#");
    		PersistenceEntryManager persistenceEntryManager = persistenceEntryManagerFactory.createEntryManager(entryManagerConf);
    		
    		persistenceEntryManagers.put(persistenceType, persistenceEntryManager);
    		operationServices.add(persistenceEntryManager.getOperationService());
    	}

		this.hybridMappingProperties = PropertiesHelper.filterProperties(conf, "#");
		
		HybridPersistenceOperationService hybridOperationService = new HybridPersistenceOperationService(operationServices);
    	
        HybridEntryManager hybridEntryManager = new HybridEntryManager(hybridMappingProperties, persistenceEntryManagers, hybridOperationService);
        LOG.info("Created HybridEntryManager: {}", hybridOperationService);

        return hybridEntryManager;
    }

	@Override
	public void initStandalone(BaseFactoryService persistanceFactoryService) {
		this.persistanceFactoryService = persistanceFactoryService;
	}

}
