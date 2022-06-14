/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.model;

import java.util.HashMap;
import java.util.Map;

import org.gluu.persist.annotation.AttributeEnum;

/**
 * Sort order
 *
 * @author Yuriy Movchan Date: 04/22/2021
 */
public enum SortOrder implements AttributeEnum {

    ASCENDING("ascending", "asc"),
    DESCENDING("descending", "desc"),
    DEFAULT("default", "def");

    private String value;
    private String shortValue;

    private static Map<String, SortOrder> MAP_BY_VALUES = new HashMap<String, SortOrder>();

    static {
        for (SortOrder enumType : values()) {
            MAP_BY_VALUES.put(enumType.getValue(), enumType);
        }
    }

    SortOrder(String value, String shortValue) {
        this.value = value;
        this.shortValue = shortValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getShortValue() {
		return shortValue;
	}

	public static SortOrder getByValue(String value) {
        return MAP_BY_VALUES.get(value);
    }

    @Override
    public SortOrder resolveByValue(String value) {
        return getByValue(value);
    }
}
