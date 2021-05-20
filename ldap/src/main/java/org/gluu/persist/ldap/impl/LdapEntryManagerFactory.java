/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap.impl;

import java.util.HashMap;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

import org.gluu.persist.PersistenceEntryManager;
import org.gluu.persist.PersistenceEntryManagerFactory;
import org.gluu.persist.exception.operation.ConfigurationException;
import org.gluu.persist.ldap.operation.impl.LdapAuthConnectionProvider;
import org.gluu.persist.ldap.operation.impl.LdapConnectionProvider;
import org.gluu.persist.ldap.operation.impl.LdapOperationServiceImpl;
import org.gluu.persist.service.BaseFactoryService;
import org.gluu.orm.util.PropertiesHelper;
import org.gluu.orm.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAP Entry Manager Factory
 *
 * @author Yuriy Movchan Date: 02/02/2018
 */
@ApplicationScoped
public class LdapEntryManagerFactory implements PersistenceEntryManagerFactory {

    public static final String PERSISTENCE_TYPE = PersistenceEntryManager.PERSITENCE_TYPES.ldap.name();
    public static final String LDAP_DEFAULT_PROPERTIES_FILE = "gluu-ldap.properties";
    public static final String PROPERTIES_FILE = "gluu-ldap%s.properties";

	private static final Logger LOG = LoggerFactory.getLogger(LdapEntryManagerFactory.class);

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
    public LdapEntryManager createEntryManager(Properties conf) {
		Properties entryManagerConf = PropertiesHelper.filterProperties(conf, "#");

		LdapConnectionProvider connectionProvider = new LdapConnectionProvider(entryManagerConf);
		connectionProvider.create();
        if (!connectionProvider.isCreated()) {
            throw new ConfigurationException(
                    String.format("Failed to create LDAP connection pool! Result code: '%s'", connectionProvider.getCreationResultCode()));
        }
        LOG.debug("Created connectionProvider '{}' with code '{}'", connectionProvider, connectionProvider.getCreationResultCode());

        LdapConnectionProvider bindConnectionProvider = new LdapAuthConnectionProvider(entryManagerConf);
		connectionProvider.create();
        if (!bindConnectionProvider.isCreated()) {
            throw new ConfigurationException(
                    String.format("Failed to create LDAP bind connection pool! Result code: '%s'", bindConnectionProvider.getCreationResultCode()));
        }
        LOG.debug("Created bindConnectionProvider '{}' with code '{}'", bindConnectionProvider, bindConnectionProvider.getCreationResultCode());

        LdapEntryManager ldapEntryManager = new LdapEntryManager(new LdapOperationServiceImpl(connectionProvider, bindConnectionProvider));
        LOG.info("Created LdapEntryManager: {}", ldapEntryManager.getOperationService());

        return ldapEntryManager;
    }

	@Override
	public void initStandalone(BaseFactoryService persistanceFactoryService) {}

}
