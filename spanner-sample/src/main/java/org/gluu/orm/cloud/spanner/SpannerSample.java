/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.cloud.spanner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gluu.persist.cloud.spanner.impl.SpannerEntryManager;
import org.gluu.persist.cloud.spanner.model.SimpleAttribute;
import org.gluu.persist.cloud.spanner.model.SimpleGrant;
import org.gluu.persist.cloud.spanner.model.SimpleSession;
import org.gluu.persist.cloud.spanner.model.SimpleUser;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerConnectionProvider;
import org.gluu.persist.cloud.spanner.persistence.SpannerEntryManagerSample;
import org.gluu.persist.exception.AuthenticationException;
import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.SortOrder;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SpannerSample {

    private static final Logger LOG = LoggerFactory.getLogger(SpannerConnectionProvider.class);

    private SpannerSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SpannerEntryManagerSample entryManagerSample = new SpannerEntryManagerSample();

        // Create SQL entry manager
        SpannerEntryManager entryManager = entryManagerSample.createSpannerEntryManager();

        SimpleUser newCustomUser = new SimpleUser();
        newCustomUser.setInum("" + System.currentTimeMillis());
        newCustomUser.setDn(String.format("inum=%s,ou=people,o=gluu", newCustomUser.getInum()));
        newCustomUser.setUserId("sample_user_" + newCustomUser.getInum());
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
        newUser.setUserPassword("pwd");
        newUser.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList("London", "Texas", "Kiev")));
        newUser.getCustomAttributes().add(new CustomObjectAttribute("transientId", "transientId"));
        newUser.getCustomAttributes().add(new CustomObjectAttribute("memberOf", Arrays.asList("1", "2", "3", "3")));
        entryManager.persist(newUser);

        SimpleUser dummyUser = entryManager.find(SimpleUser.class, newUser.getDn());
        LOG.info("Dummy User '{}'", dummyUser);

        String[] values = new String[] { "Somewhere: " + System.currentTimeMillis(), "Somewhere2: " + System.currentTimeMillis() };
        dummyUser.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList(values)));
        dummyUser.getCustomAttributes().add(new CustomObjectAttribute("transientId", "new_transientId"));
        dummyUser.getCustomAttributes().add(new CustomObjectAttribute("gluuGuid", "test_guid"));
        dummyUser.getCustomAttributes().add(new CustomObjectAttribute("memberOf", Arrays.asList("1", "9")));
        dummyUser.setUserId("user1");
        dummyUser.setUserPassword("test_pwd");
        entryManager.merge(dummyUser);
/*
        Filter filterX1 = Filter.createEqualityFilter("transientId", "new_transientId");
        List<SimpleUser> usersss = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filterX1);
        System.out.println(usersss.size());
*/
        Filter filterX = Filter.createEqualityFilter("memberOf", "1");
        List<SimpleUser> userss = entryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filterX, new String[] { "memberOf", "uid" }, 10);
        System.out.println(userss.size());

        Filter filterR = Filter.createEqualityFilter("gluuGuid", "test_guid");
        int removed = entryManager.remove("ou=people,o=gluu", SimpleUser.class, filterR, 10);
        System.out.println(removed);
//        sqlEntryManager.remove(dummyUser);
        entryManager.destroy();
        System.exit(0);

        // Find all users which have specified object classes defined in SimpleUser
        List<SimpleUser> users = entryManager.findEntries("ou=people,o=gluu", SimpleUser.class, null);
        for (SimpleUser user : users) {
            LOG.info("User with uid: '{}' with DN: '{}'", user.getUserId(), user.getDn());
        }
/*
        if (users.size() > 0) {
            // Add attribute "address" to first user
            SimpleUser user = users.get(0);
            LOG.info("Updating: " + user.getUserId());
 
            String[] values = new String[] { "Somewhere: " + System.currentTimeMillis(), "Somewhere2: " + System.currentTimeMillis() };
            user.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList(values)));
            user.getCustomAttributes().add(new CustomObjectAttribute("transientId", "new_transientId"));
            user.getCustomAttributes().add(new CustomObjectAttribute("gluuGuid", "test_guid"));
            user.setUserId("user1");
            user.setUserPassword("test_pwd");

            sqlEntryManager.merge(user);
        }
*/
        for (SimpleUser user : users) {
            boolean result1 = entryManager.authenticate(user.getDn(), SimpleUser.class, "test_pwd");
            boolean result2 = entryManager.authenticate("ou=people,o=gluu", SimpleUser.class, user.getUserId(), "test");
            System.out.println("authetication result: " + result1 + ", " + result2);
        }

        Filter filter = Filter.createEqualityFilter("gluuStatus", "active");
        List<SimpleAttribute> attributes = entryManager.findEntries("o=gluu", SimpleAttribute.class, filter, SearchScope.SUB, null, null, 10,
                0, 0);
        for (SimpleAttribute attribute : attributes) {
            LOG.info("Attribute with displayName: " + attribute.getCustomAttributes().get(1));
        }

        Filter filter2 = Filter.createEqualityFilter("gluuState", "authenticated");
        List<SimpleSession> sessions = entryManager.findEntries("o=gluu", SimpleSession.class, filter2, SearchScope.SUB, null, null, 10, 0,
                0);
        LOG.info("Found sessions: " + sessions.size());

        List<SimpleGrant> grants = entryManager.findEntries("o=gluu", SimpleGrant.class, null, SearchScope.SUB,
                new String[] { "grtId" }, null, 1, 0, 0);
        LOG.info("Found grants: " + grants.size());

        try {
            PagedResult<SimpleUser> listViewResponse = entryManager.findPagedEntries("o=gluu", SimpleUser.class, null,
                    new String[] { "uid", "displayName", "gluuStatus" }, "uid", SortOrder.ASCENDING, 0, 6, 4);

            LOG.info("Found persons: " + listViewResponse.getEntriesCount() + ", total persons: " + listViewResponse.getTotalEntriesCount());
            for (SimpleUser user : listViewResponse.getEntries()) {
                System.out.println(user.getUserId());
            }
        } catch (Exception ex) {
            LOG.info("Failed to search", ex);
        }

        try {
            PagedResult<SimpleUser> listViewResponse = entryManager.findPagedEntries("o=gluu", SimpleUser.class, null,
                    new String[] { "uid", "displayName", "gluuStatus" }, "uid", SortOrder.DESCENDING, 0, 6, 4);

            LOG.info("Found persons: " + listViewResponse.getEntriesCount() + ", total persons: " + listViewResponse.getTotalEntriesCount());
            for (SimpleUser user : listViewResponse.getEntries()) {
                System.out.println(user.getUserId());
            }
        } catch (Exception ex) {
            LOG.info("Failed to search", ex);
        }

    }

}
