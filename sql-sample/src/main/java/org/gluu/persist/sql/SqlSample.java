/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gluu.orm.util.StringHelper;
import org.gluu.persist.exception.AuthenticationException;
import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.model.SortOrder;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleAttribute;
import org.gluu.persist.sql.model.SimpleGrant;
import org.gluu.persist.sql.model.SimpleSession;
import org.gluu.persist.sql.model.SimpleUser;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private SqlSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        SqlEntryManagerSample entryManagerSample = new SqlEntryManagerSample();

        // Create SQL entry manager
        SqlEntryManager entryManager = entryManagerSample.createSqlEntryManager();

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

        Filter filter0 = Filter.createLessOrEqualFilter("associatedClient", 7).multiValued();
        List<SimpleUser> users0 = entryManager.findEntries("o=gluu", SimpleUser.class, filter0, SearchScope.SUB, null, null, 0,
                0, 0);
        for (SimpleUser user : users0) {
            LOG.info("User XXX with '{}', associatedClient: '{}'", user.getUserId(), user.getAttributeValues("associatedClient"));
        }

        SimpleUser newUser = new SimpleUser();
        newUser.setDn(String.format("inum=%s,ou=people,o=gluu", System.currentTimeMillis()));
        newUser.setUserId("sample_user_" + System.currentTimeMillis());
        newUser.setUserPassword("pwd");
        newUser.setMemberOf(Arrays.asList("member_1", "member_2", "member_3"));
        newUser.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList("London", "Texas", "Kiev")));
        newUser.getCustomAttributes().add(new CustomObjectAttribute("transientId", "transientId"));
        entryManager.persist(newUser);

        SimpleUser dummyUser = entryManager.find(SimpleUser.class, newUser.getDn());
        LOG.info("Dummy User '{}' with userId '{}'", dummyUser, newUser.getUserId());

        // Find all users which have specified object classes defined in SimpleUser
        List<SimpleUser> users = entryManager.findEntries("ou=people,o=gluu", SimpleUser.class, null);
        for (SimpleUser user : users) {
            LOG.info("User with uid: '{}' with DN: '{}'", user.getUserId(), user.getDn());
        }

        if (users.size() > 0) {
        	for (SimpleUser user : users) {
        		if (StringHelper.equalsIgnoreCase(newUser.getDn(), user.getDn())) {
		            // Update attribute "address" to first user
		            LOG.info("Updating: " + user.getUserId());
		 
		            String[] values = new String[] { "Somewhere: " + System.currentTimeMillis(), "Somewhere2: " + System.currentTimeMillis() };
		            user.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList(values)));
		            user.getCustomAttributes().add(new CustomObjectAttribute("transientId", "new_transientId"));
		            user.getCustomAttributes().add(new CustomObjectAttribute("oxGuid", "test_guid"));
		            user.setUserId(newUser.getUserId() + "_new");
		            user.setUserPassword("test_pwd");
		            user.setMemberOf(Arrays.asList("member_3", "member_4", "member_5"));
		
		            entryManager.merge(user);
		            break;
        		}
        	}
        }

        SimpleUser dummyUserSearch = entryManager.find(SimpleUser.class, newUser.getDn());
        LOG.info("Dummy User address: '{}', memberOf: '{}', transientId: '{}', oxGuid: '{}'", dummyUserSearch.getAttribute("address"), dummyUserSearch.getMemberOf(),
        		dummyUserSearch.getAttribute("transientId"), dummyUserSearch.getAttribute("oxGuid"));

        for (SimpleUser user : users) {
            boolean result1 = entryManager.authenticate(user.getDn(), SimpleUser.class, "test_pwd");
            boolean result2 = entryManager.authenticate("ou=people,o=gluu", SimpleUser.class, user.getUserId(), "test");
            System.out.println("authetication result: " + result1 + ", " + result2);
        }

        Filter filter1 = Filter.createEqualityFilter("memberOf", "member_4").multiValued();
        List<SimpleUser> users1 = entryManager.findEntries("o=gluu", SimpleUser.class, filter1, SearchScope.SUB, null, null, 0,
                0, 0);
        for (SimpleUser user : users1) {
            LOG.info("User with '{}', memberOf: '{}'", user.getUserId(), user.getMemberOf());
        }

        Filter filter2 = Filter.createEqualityFilter("gluuStatus", "active");
        List<SimpleAttribute> attributes2 = entryManager.findEntries("o=gluu", SimpleAttribute.class, filter2, SearchScope.SUB, null, null, 10,
                0, 0);
        for (SimpleAttribute attribute : attributes2) {
            LOG.info("Attribute with displayName: " + attribute.getCustomAttributes().get(1));
        }

        Filter filter3 = Filter.createEqualityFilter("oxState", "authenticated");
        List<SimpleSession> sessions = entryManager.findEntries("o=gluu", SimpleSession.class, filter3, SearchScope.SUB, null, null, 10, 0,
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
