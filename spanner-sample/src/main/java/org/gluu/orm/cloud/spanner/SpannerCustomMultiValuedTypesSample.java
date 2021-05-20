/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.cloud.spanner;

import java.util.Arrays;
import java.util.List;

import org.gluu.persist.cloud.spanner.impl.SpannerEntryManager;
import org.gluu.persist.cloud.spanner.model.SimpleUser;
import org.gluu.persist.cloud.spanner.operation.impl.SpannerConnectionProvider;
import org.gluu.persist.cloud.spanner.persistence.SpannerEntryManagerSample;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.search.filter.Filter;
import org.gluu.orm.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SpannerCustomMultiValuedTypesSample {

	private static final Logger LOG = LoggerFactory.getLogger(SpannerConnectionProvider.class);

	private SpannerCustomMultiValuedTypesSample() {
	}

	public static void main(String[] args) {
		// Prepare sample connection details
		SpannerEntryManagerSample sqlEntryManagerSample = new SpannerEntryManagerSample();

		// Create SQL entry manager
		SpannerEntryManager sqlEntryManager = sqlEntryManagerSample.createSpannerEntryManager();

		// Add dummy user
		SimpleUser newUser = new SimpleUser();
		newUser.setDn(String.format("inum=%s,ou=people,o=gluu", System.currentTimeMillis()));
		newUser.setUserId("sample_user_" + System.currentTimeMillis());
		newUser.setUserPassword("test");
		newUser.getCustomAttributes().add(new CustomObjectAttribute("gluuOptOuts", Arrays.asList("London", "Texas", "Kiev")));
		newUser.getCustomAttributes().add(new CustomObjectAttribute("gluuExtUid", "test_value").multiValued());
		newUser.getCustomAttributes().add(new CustomObjectAttribute("gluuPPID", "test_value").multiValued());
		newUser.setMemberOf(Arrays.asList("group_1", "group_2", "group_3"));
		newUser.setAttributeValue("givenName", "john");

		sqlEntryManager.persist(newUser);

		LOG.info("Added User '{}' with uid '{}' and key '{}'", newUser, newUser.getUserId(), newUser.getDn());
		LOG.info("Persisted custom attributes '{}'", newUser.getCustomAttributes());

		// Find added dummy user
		SimpleUser foundUser = sqlEntryManager.find(SimpleUser.class, newUser.getDn());
		LOG.info("Found User '{}' with uid '{}' and key '{}'", foundUser, foundUser.getUserId(), foundUser.getDn());
		LOG.info("Custom attributes '{}'", foundUser.getCustomAttributes());

		// Dump custom attributes
		for (CustomObjectAttribute attr : foundUser.getCustomAttributes()) {
			System.out.println(attr.getName() + " - " + attr.getValues());
		}

		// Update custom attributes
		foundUser.setAttributeValues("gluuOptOuts", Arrays.asList("London", "Texas", "Kiev", "Dublin"));
		foundUser.setAttributeValues("gluuExtUid", Arrays.asList("test_value_11", "test_value_22", "test_value_33", "test_value_44"));
		foundUser.setAttributeValues("gluuExtUid", Arrays.asList(11, 22, 33, 44));
		foundUser.setAttributeValues("gluuPPID", Arrays.asList("fuzzy_value_1", "fuzzy_value_2"));
		foundUser.setAttributeValue("gluuGuid", "simple");
		
		CustomObjectAttribute multiValuedSingleValue = new CustomObjectAttribute("gluuAssociatedClnt", "multivalued_single_valued");
		multiValuedSingleValue.setMultiValued(true);
		foundUser.getCustomAttributes().add(multiValuedSingleValue);
		sqlEntryManager.merge(foundUser);
		LOG.info("Updated custom attributes '{}'", foundUser.getCustomAttributes());

		// Find updated dummy user
		SimpleUser foundUpdatedUser = sqlEntryManager.find(SimpleUser.class, newUser.getDn());
		LOG.info("Found User '{}' with uid '{}' and key '{}'", foundUpdatedUser, foundUpdatedUser.getUserId(), foundUpdatedUser.getDn());
		LOG.info("Cusom attributes '{}'", foundUpdatedUser.getCustomAttributes());

		// Dump custom attributes
		for (CustomObjectAttribute attr : foundUser.getCustomAttributes()) {
			System.out.println(attr.getName() + " - " + attr.getValues());
		}

		Filter filter = Filter.createEqualityFilter(Filter.createLowercaseFilter("givenName"), StringHelper.toLowerCase("john"));
		List<SimpleUser> foundUpdatedUsers = sqlEntryManager.findEntries("o=gluu", SimpleUser.class, filter);
		System.out.println(foundUpdatedUsers);
	}

}
