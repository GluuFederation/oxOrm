/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql.model;

import java.util.ArrayList;
import java.util.List;

import org.gluu.persist.model.base.CustomAttribute;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

@JsonPropertyOrder({ "type", "name", "level", "priority", "enabled", "version", "fields", "config" })
public class IDPAuthConf {
	private String type;
	private String name;

	private int level;
	private int priority;
	private boolean enabled;
	private List<CustomAttribute> fields;
	private int version;

	private JsonNode config;

	public IDPAuthConf() {
		this.fields = new ArrayList<CustomAttribute>();
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<CustomAttribute> getFields() {
		return this.fields;
	}

	public void setFields(List<CustomAttribute> fields) {
		this.fields = fields;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public JsonNode getConfig() {
		return config;
	}

	public void setConfig(JsonNode config) {
		this.config = config;
	}

}
