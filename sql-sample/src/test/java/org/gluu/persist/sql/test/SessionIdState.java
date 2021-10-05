/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql.test;

import java.util.HashMap;
import java.util.Map;

import org.gluu.persist.annotation.AttributeEnum;

/**
*
* @author Yuriy Movchan Date: 01/15/2020
*/
public enum SessionIdState implements AttributeEnum {

    UNAUTHENTICATED("unauthenticated"), AUTHENTICATED("authenticated");

    private final String value;

    private static Map<String, SessionIdState> mapByValues = new HashMap<String, SessionIdState>();

    static {
        for (SessionIdState enumType : values()) {
            mapByValues.put(enumType.getValue(), enumType);
        }
    }

    private SessionIdState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SessionIdState getByValue(String value) {
        return mapByValues.get(value);
    }

    public Enum<? extends AttributeEnum> resolveByValue(String value) {
        return getByValue(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
