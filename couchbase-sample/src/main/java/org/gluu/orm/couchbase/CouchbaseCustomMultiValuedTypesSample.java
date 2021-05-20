/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import java.util.Arrays;
import java.util.List;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.SimpleUser;
import org.gluu.orm.couchbase.operation.impl.CouchbaseConnectionProvider;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.search.filter.Filter;
import org.gluu.orm.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 09/16/2019
 */
public final class CouchbaseCustomMultiValuedTypesSample {

	private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

	private CouchbaseCustomMultiValuedTypesSample() {
	}

	public static void main(String[] args) {
		// Prepare sample connection details
		CouchbaseEntryManagerSample couchbaseEntryManagerSample = new CouchbaseEntryManagerSample();

		// Create Couchbase entry manager
		CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerSample.createCouchbaseEntryManager();

		// Add dummy user
		SimpleUser newUser = new SimpleUser();
		newUser.setDn(String.format("inum=%s,ou=people,o=gluu", System.currentTimeMillis()));
		newUser.setUserId("sample_user_" + System.currentTimeMillis());
		newUser.setUserPassword("test");
		newUser.getCustomAttributes().add(new CustomObjectAttribute("streetAddress", Arrays.asList("London", "Texas", "Kiev")));
		newUser.getCustomAttributes().add(new CustomObjectAttribute("test", "test_value"));
		newUser.getCustomAttributes().add(new CustomObjectAttribute("fuzzy", "test_value"));
		newUser.setMemberOf(Arrays.asList("group_1", "group_2", "group_3"));
		newUser.setAttributeValue("givenName", "john");

		couchbaseEntryManager.persist(newUser);

		LOG.info("Added User '{}' with uid '{}' and key '{}'", newUser, newUser.getUserId(), newUser.getDn());
		LOG.info("Persisted custom attributes '{}'", newUser.getCustomAttributes());

		// Find added dummy user
		SimpleUser foundUser = couchbaseEntryManager.find(SimpleUser.class, newUser.getDn());
		LOG.info("Found User '{}' with uid '{}' and key '{}'", foundUser, foundUser.getUserId(), foundUser.getDn());

		LOG.info("Custom attributes '{}'", foundUser.getCustomAttributes());

		// Update custom attributes
		foundUser.setAttributeValues("streetAddress", Arrays.asList("London", "Texas", "Kiev", "Dublin"));
		foundUser.setAttributeValues("test", Arrays.asList("test_value_1", "test_value_2", "test_value_3", "test_value_4"));
		foundUser.setAttributeValues("fuzzy", Arrays.asList("fuzzy_value_1", "fuzzy_value_2"));
		foundUser.setAttributeValue("simple", "simple");
		
		CustomObjectAttribute multiValuedSingleValue = new CustomObjectAttribute("multivalued", "multivalued_single_valued");
		multiValuedSingleValue.setMultiValued(true);
		foundUser.getCustomAttributes().add(multiValuedSingleValue);
		couchbaseEntryManager.merge(foundUser);
		LOG.info("Updated custom attributes '{}'", foundUser.getCustomAttributes());

		// Find updated dummy user
		SimpleUser foundUpdatedUser = couchbaseEntryManager.find(SimpleUser.class, newUser.getDn());
		LOG.info("Found User '{}' with uid '{}' and key '{}'", foundUpdatedUser, foundUpdatedUser.getUserId(), foundUpdatedUser.getDn());

		LOG.info("Cusom attributes '{}'", foundUpdatedUser.getCustomAttributes());

		Filter filter = Filter.createEqualityFilter(Filter.createLowercaseFilter("givenName"), StringHelper.toLowerCase("jon"));
		List<SimpleUser> foundUpdatedUsers = couchbaseEntryManager.findEntries("o=gluu", SimpleUser.class, filter);
		System.out.println(foundUpdatedUsers);
		
	}

}
