package org.gluu.search.filter;
/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2021, Gluu
 */

import java.util.LinkedList;
import java.util.List;

import org.gluu.orm.util.StringHelper;

/**
 * Filter processor
 *
 * @author Yuriy Movchan Date: 03/18/2021
 */
public class FilterProcessor {

	public static final Filter OBJECT_CLASS_EQUALITY_FILTER = Filter.createEqualityFilter("objectClass", null);
	public static final Filter OBJECT_CLASS_PRESENCE_FILTER = Filter.createPresenceFilter("objectClass");

	public Filter excludeFilter(Filter genericFilter, Filter... excludeFilters) {
		if (genericFilter == null) {
			return null;
		}

		FilterType type = genericFilter.getType();
		if (FilterType.RAW == type) {
			return genericFilter;
		}

		Filter[] filters = genericFilter.getFilters();
		if (filters != null) {
			List<Filter> resultFilters = new LinkedList<>();
			for (Filter filter : filters) {
				Filter resultFilter = excludeFilter(filter, excludeFilters);
				if (resultFilter != null) {
					resultFilters.add(resultFilter);
				}
			}
			if (resultFilters.size() == 0) {
				return null;
			}

			Filter resultFilter = new Filter(type, resultFilters.toArray(new Filter[0]));
			resultFilter.setAssertionValue(genericFilter.getAssertionValue());

			return resultFilter;
		}

		// Check if current filter conform filter specified in excludeFilter
		for (Filter excludeFilter : excludeFilters) {
			boolean typeMatch = (excludeFilter.getType() == null) || (excludeFilter.getType() == type);
			boolean nameMatch = StringHelper.isEmpty(excludeFilter.getAttributeName()) || StringHelper.equalsIgnoreCase(excludeFilter.getAttributeName(), genericFilter.getAttributeName());
			boolean valueMatch = StringHelper.isEmptyString(excludeFilter.getAssertionValue())|| excludeFilter.getAssertionValue().equals(genericFilter.getAssertionValue());
	
			if (typeMatch && nameMatch && valueMatch) {
				return null;
			}
		}

		return genericFilter;
	}

}