/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.gluu.orm.search.filter.Filter;
import org.gluu.persist.ldap.impl.LdapEntryManager;
import org.gluu.persist.ldap.model.SimpleAttribute;
import org.gluu.persist.ldap.model.SimpleGrant;
import org.gluu.persist.ldap.model.SimpleSession;
import org.gluu.persist.ldap.model.SimpleUser;
import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.SortOrder;
import org.gluu.persist.model.base.CustomAttribute;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class LdapSample {

    private static final Logger LOG;

    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
        LoggingHelper.configureConsoleAppender();
        LOG = Logger.getLogger(LdapSample.class);
    }

    private LdapSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        LdapEntryManagerSample ldapEntryManagerSample = new LdapEntryManagerSample();

        // Create LDAP entry manager
        LdapEntryManager ldapEntryManager = ldapEntryManagerSample.createLdapEntryManager();

        // Find all users which have specified object classes defined in SimpleUser
        List<SimpleUser> users = ldapEntryManager.findEntries("o=jans", SimpleUser.class, null);
        for (SimpleUser user : users) {
            LOG.debug("User with uid: " + user.getUserId());
        }

        if (users.size() > 0) {
            // Add attribute "streetAddress" to first user
            SimpleUser user = users.get(0);
            user.getCustomAttributes().add(new CustomAttribute("streetAddress", "Somewhere: " + System.currentTimeMillis()));

            ldapEntryManager.merge(user);
        }

        Filter filter = Filter.createEqualityFilter("status", "active");
        List<SimpleAttribute> attributes = ldapEntryManager.findEntries("o=jans", SimpleAttribute.class, filter, SearchScope.SUB, null, null, 10, 0,
                0);
        for (SimpleAttribute attribute : attributes) {
            LOG.debug("Attribute with displayName: " + attribute.getCustomAttributes().get(1));
        }

        List<SimpleSession> sessions = ldapEntryManager.findEntries("o=jans", SimpleSession.class, filter, SearchScope.SUB, null, null, 10, 0, 0);
        LOG.debug("Found sessions: " + sessions.size());

        List<SimpleGrant> grants = ldapEntryManager.findEntries("o=jans", SimpleGrant.class, null, SearchScope.SUB, new String[] { "grtId" },
                null, 10, 0, 0);
        LOG.debug("Found grants: " + grants.size());

        try {
            PagedResult<SimpleUser> vlvResponse = ldapEntryManager.findPagedEntries("o=jans", SimpleUser.class, null,
                    new String[] { "uid", "displayName", "status" }, "displayName", SortOrder.ASCENDING, 10, 100000, 1000);

            LOG.debug("Found persons: " + vlvResponse.getTotalEntriesCount());
            System.out.println(vlvResponse.getEntries().size());
        } catch (Exception ex) {
            LOG.error("Failed to search", ex);
        }
    }

}
