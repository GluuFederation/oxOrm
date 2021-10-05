/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.persist.ldap.impl.LdapEntryManager;
import org.gluu.persist.ldap.model.MailUniquenessConfiguration;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class MailUniquenessConfigurationSample {

    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(MailUniquenessConfigurationSample.class);
    }

    private MailUniquenessConfigurationSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        LdapEntryManagerSample ldapEntryManagerSample = new LdapEntryManagerSample();

        // Create LDAP entry manager
        LdapEntryManager ldapEntryManager = ldapEntryManagerSample.createLdapEntryManager();

        MailUniquenessConfiguration conf = ldapEntryManager.find("cn=Unique mail address,cn=Plugins,cn=config", MailUniquenessConfiguration.class, null);
        System.out.println("Current mail uniqueness: " + conf.isEnabled());
        
        conf.setEnabled(!conf.isEnabled());

        // Upate configuration in LDAP
        ldapEntryManager.merge(conf);

        MailUniquenessConfiguration conf2 = ldapEntryManager.find("cn=Unique mail address,cn=Plugins,cn=config", MailUniquenessConfiguration.class, null);
        System.out.println("After update mail uniqueness: " + conf2.isEnabled());
    }

}
