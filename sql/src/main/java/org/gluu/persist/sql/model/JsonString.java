package org.gluu.persist.sql.model;

/**
 * PostgreSQL JSON column type support
 *
 * @author Yuriy Movchan Date: 09/01/2022
 */

public class JsonString {
	
	private String value;

	public JsonString(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
