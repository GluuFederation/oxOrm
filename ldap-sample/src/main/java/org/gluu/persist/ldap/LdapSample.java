/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gluu.persist.exception.AuthenticationException;
import org.gluu.persist.ldap.impl.LdapEntryManager;
import org.gluu.persist.ldap.model.SimpleAttribute;
import org.gluu.persist.ldap.model.SimpleGrant;
import org.gluu.persist.ldap.model.SimpleSession;
import org.gluu.persist.ldap.model.SimpleUser;
import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.SortOrder;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class LdapSample {


    private static final Logger LOG = LoggerFactory.getLogger(LdapSample.class);

    private LdapSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        LdapEntryManagerSample entryManagerSample = new LdapEntryManagerSample();

        // Create LDAP entry manager
        LdapEntryManager entryManager = entryManagerSample.createLdapEntryManager();

        SimpleUser newCustomUser = new SimpleUser();
        newCustomUser.setDn(String.format("inum=%s,ou=people,o=gluu", System.currentTimeMillis()));
        newCustomUser.setUserId("sample_user_" + System.currentTimeMillis());
        newCustomUser.setUserPassword("pwd");
        newCustomUser.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList("London", "Texas", "Kiev")));
        newCustomUser.getCustomAttributes().add(new CustomObjectAttribute("transientId", "transientId"));
        newCustomUser.getCustomAttributes().add(new CustomObjectAttribute("scimCustomThird", 100));
        newCustomUser.getCustomAttributes().add(new CustomObjectAttribute("phoneNumberVerified", true));
        newCustomUser.getCustomAttributes().add(new CustomObjectAttribute("updatedAt", new Date()));
        
        entryManager.persist(newCustomUser);

        SimpleUser dummyCustomUser = entryManager.find(SimpleUser.class, newCustomUser.getDn());
        LOG.info("Dummy User '{}' with userId '{}'", dummyCustomUser, newCustomUser.getUserId());

        boolean authenticated = entryManager.authenticate(newCustomUser.getDn(), SimpleUser.class, "pwd");
        LOG.info("Dummy User success authentication '{}'", authenticated);

        try {
			boolean authenticated2 = entryManager.authenticate(newCustomUser.getDn(), SimpleUser.class, "pwd2");
			LOG.info("Dummy User wrong authentication '{}'", authenticated2);
		} catch (AuthenticationException ex) {
			LOG.error(ex.getMessage());
		}

        // Find all users which have specified object classes defined in SimpleUser
        List<SimpleUser> users = entryManager.findEntries("o=gluu", SimpleUser.class, null);
        for (SimpleUser user : users) {
            LOG.debug("User with uid: " + user.getUserId());
        }

        if (users.size() > 0) {
            // Add attribute "streetAddress" to first user
            SimpleUser user = users.get(0);
            user.getCustomAttributes().add(new CustomObjectAttribute("streetAddress", "Somewhere: " + System.currentTimeMillis()));

            entryManager.merge(user);
        }

        Filter filter = Filter.createEqualityFilter("status", "active");
        List<SimpleAttribute> attributes = entryManager.findEntries("o=gluu", SimpleAttribute.class, filter, SearchScope.SUB, null, null, 10, 0,
                0);
        for (SimpleAttribute attribute : attributes) {
            LOG.debug("Attribute with displayName: " + attribute.getCustomAttributes().get(1));
        }

        List<SimpleSession> sessions = entryManager.findEntries("o=gluu", SimpleSession.class, filter, SearchScope.SUB, null, null, 10, 0, 0);
        LOG.debug("Found sessions: " + sessions.size());

        List<SimpleGrant> grants = entryManager.findEntries("o=gluu", SimpleGrant.class, null, SearchScope.SUB, new String[] { "grtId" },
                null, 10, 0, 0);
        LOG.debug("Found grants: " + grants.size());

        try {
            PagedResult<SimpleUser> vlvResponse = entryManager.findPagedEntries("o=gluu", SimpleUser.class, null,
                    new String[] { "uid", "displayName", "status" }, "displayName", SortOrder.ASCENDING, 10, 100000, 1000);

            LOG.debug("Found persons: " + vlvResponse.getTotalEntriesCount());
            System.out.println(vlvResponse.getEntries().size());
        } catch (Exception ex) {
            LOG.error("Failed to search", ex);
        }
    }

}
