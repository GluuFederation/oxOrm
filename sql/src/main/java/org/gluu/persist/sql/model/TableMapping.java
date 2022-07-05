/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql.model;

import java.util.Map;

import org.gluu.persist.model.AttributeType;

/**
 * Mapping to DB table
 *
 * @author Yuriy Movchan Date: 12/22/2020
 */
public class TableMapping {

    private final String baseKeyName;
    private final String tableName;
    private final String objectClass;
    private final Map<String, AttributeType> columTypes;

    public TableMapping(final String baseKeyName, final String tableName, final String objectClass, Map<String, AttributeType> columTypes) {
        this.baseKeyName = baseKeyName;
        this.tableName = tableName;
        this.objectClass = objectClass;
        this.columTypes = columTypes;
    }

	public String getBaseKeyName() {
		return baseKeyName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public Map<String, AttributeType> getColumTypes() {
		return columTypes;
	}

}
