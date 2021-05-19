/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.search.filter;

/**
 * Filter operation types
 *
 * @author Yuriy Movchan Date: 2017/12/13
 */
public enum FilterType {

    RAW(""), PRESENCE("*"), EQUALITY("="), LESS_OR_EQUAL("<="), GREATER_OR_EQUAL(">="), APPROXIMATE_MATCH("~"), SUBSTRING("="), NOT("!"),
    OR("|"), AND("&"), LOWERCASE("lowercase");

    private String sign;

    FilterType(String sign) {
        this.sign = sign;
    }

    public final String getSign() {
        return sign;
    }

}
