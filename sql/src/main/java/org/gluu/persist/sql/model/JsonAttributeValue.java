/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON DB value
 *
 * @author Yuriy Movchan Date: 03/02/2021
 */
public class JsonAttributeValue {

	@JsonProperty("v")
    private Object[] values;

	public JsonAttributeValue() {
	}

	public JsonAttributeValue(Object[] values) {
		this.values = values;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

}
