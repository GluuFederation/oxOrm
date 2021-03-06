/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.cloud.spanner.model;

import com.google.cloud.spanner.Type.StructField;

/**
 * Mapping to DB table
 *
 * @author Yuriy Movchan Date: 12/22/2020
 */
public class ValueWithStructField {

	private final Object value;
	private final StructField structField;

	public ValueWithStructField(Object value, StructField structField) {
		super();
		this.value = value;
		this.structField = structField;
	}

	public Object getValue() {
		return value;
	}

	public StructField getStructField() {
		return structField;
	}

	@Override
	public String toString() {
		return "ValueWithStructField [value=" + value + ", structField=" + structField.getType().getCode() + "]";
	}

}
