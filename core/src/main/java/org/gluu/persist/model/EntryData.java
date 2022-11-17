/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.model;

import java.util.List;

/**
 * DB row with attribues
 *
 * @author Yuriy Movchan Date: 01/19/2021
 */
public class EntryData {
    private final List<AttributeData> attributeData;
	private String dn;

    public EntryData(List<AttributeData> attributeData) {
        this.attributeData = attributeData;
    }

    public EntryData(String dn, List<AttributeData> attributeData) {
        this.dn = dn;
        this.attributeData = attributeData;
    }

	public List<AttributeData> getAttributeData() {
		return attributeData;
	}

	public String getDN() {
		return dn;
	}

	public AttributeData getAttributeData(String internalAttribute) {
		if (attributeData == null) {
			return null;
		}
		
		for (AttributeData attributeData : attributeData) {
			if (internalAttribute.equalsIgnoreCase(attributeData.getName())) {
				return attributeData;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "EntryData [attributeData=" + attributeData + ", dn=" + dn + "]";
	}

}
