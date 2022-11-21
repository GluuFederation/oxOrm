/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.SimpleAttribute;
import org.gluu.orm.couchbase.model.SimpleSession;
import org.gluu.orm.couchbase.model.SimpleToken;
import org.gluu.orm.couchbase.model.SimpleUser;
import org.gluu.orm.couchbase.operation.impl.CouchbaseConnectionProvider;
import org.gluu.persist.exception.AuthenticationException;
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
public final class CouchbaseSample {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

    private CouchbaseSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        CouchbaseEntryManagerSample entryManagerSample = new CouchbaseEntryManagerSample();

        // Create Couchbase entry manager
        CouchbaseEntryManager entryManager = entryManagerSample.createCouchbaseEntryManager();

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

        SimpleUser newUser = new SimpleUser();
        newUser.setDn(String.format("inum=%s,ou=people,o=gluu", System.currentTimeMillis()));
        newUser.setUserId("sample_user_" + System.currentTimeMillis());
        newUser.setUserPassword("test");
        newUser.getCustomAttributes().add(new CustomObjectAttribute("streetAddress", Arrays.asList("London", "Texas", "Kiev")));
        newUser.getCustomAttributes().add(new CustomObjectAttribute("test", "test_value"));
        entryManager.persist(newUser);

        // Find all users which have specified object classes defined in SimpleUser
        List<SimpleUser> users = entryManager.findEntries("ou=people,o=gluu", SimpleUser.class, null);
        for (SimpleUser user : users) {
            LOG.info("User with uid: '{}' with DN: '{}'", user.getUserId(), user.getDn());
        }

        if (users.size() > 0) {
            // Add attribute "streetAddress" to first user
            SimpleUser user = users.get(3);
            LOG.info("Updating: " + user.getUserId());

            String[] values = new String[] { "Somewhere: " + System.currentTimeMillis(), "Somewhere2: " + System.currentTimeMillis() };
            user.getCustomAttributes().add(new CustomObjectAttribute("streetAddress", Arrays.asList(values)));
            user.getCustomAttributes().add(new CustomObjectAttribute("test", "test_value"));
            user.getCustomAttributes().add(new CustomObjectAttribute("test2", "test_value2"));
            user.getCustomAttributes().add(new CustomObjectAttribute("test3", "test_value3"));
            user.setUserId("user1");
            user.setUserPassword("test");

            entryManager.merge(user);
        }

        for (SimpleUser user : users) {
            boolean result1 = entryManager.authenticate(user.getDn(), "test");
            boolean result2 = entryManager.authenticate("ou=people,o=gluu", SimpleUser.class, user.getUserId(), "test");
            System.out.println("authetication result: " + result1 + ", " + result2 + ", user: " + user.getDn());
        }

        Filter filterAttributes = Filter.createEqualityFilter("gluuStatus", "active");
        List<SimpleAttribute> attributes = entryManager.findEntries("ou=attributes,o=gluu", SimpleAttribute.class, filterAttributes, SearchScope.SUB, null, null, 10,
                0, 0);
        LOG.info("Found attributes: " + attributes.size());
        for (SimpleAttribute attribute : attributes) {
            LOG.info("Attribute with displayName: " + attribute.getCustomAttributes().get(1));
        }

        Filter filterSessions = Filter.createEqualityFilter("oxState", "authenticated");
        List<SimpleSession> sessions = entryManager.findEntries("ou=sessions,o=gluu", SimpleSession.class, filterSessions, SearchScope.SUB, null, null, 0, 1,
                0);
        LOG.info("Found sessions: " + sessions.size());

        List<SimpleToken> tokens = entryManager.findEntries("ou=tokens,o=gluu", SimpleToken.class, null, SearchScope.SUB,
                new String[] { "code" }, null, 1, 0, 0);
        LOG.info("Found tokens: " + tokens.size());

        try {
            PagedResult<SimpleUser> listViewResponse = entryManager.findPagedEntries("ou=people,o=gluu", SimpleUser.class, null,
                    new String[] { "uid", "displayName", "status" }, "uid", SortOrder.ASCENDING, 0, 6, 4);

            LOG.info("Found persons: " + listViewResponse.getEntriesCount() + ", total persons: " + listViewResponse.getTotalEntriesCount());
            for (SimpleUser user : listViewResponse.getEntries()) {
                System.out.println(user.getUserId());
            }
        } catch (Exception ex) {
            LOG.info("Failed to search", ex);
        }

        try {
            PagedResult<SimpleUser> listViewResponse = entryManager.findPagedEntries("ou=people,o=gluu", SimpleUser.class, null,
                    new String[] { "uid", "displayName", "status" }, "uid", SortOrder.DESCENDING, 0, 6, 4);

            LOG.info("Found persons: " + listViewResponse.getEntriesCount() + ", total persons: " + listViewResponse.getTotalEntriesCount());
            for (SimpleUser user : listViewResponse.getEntries()) {
                System.out.println(user.getUserId());
            }
        } catch (Exception ex) {
            LOG.info("Failed to search", ex);
        }

    }

}
