/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark POJO class as Persistance entry
 *
 * @author Yuriy Movchan Date: 10.07.2010
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataEntry {

	/**
     * (Optional) The name of the Persistance attributes
     */
	String[] sortByName() default {};

    /**
     * (Optional) Specify that this entry contains LDAP configuration definition.
     */
    boolean configurationDefinition() default false;

    /**
     * (Optional) Specify sortBy properties to sort by default list of Entries.
     */
    String[] sortBy() default {};

    /**
     * (Optional) Specify that ORM should update entry without it lookup.
     */
    boolean forceUpdate() default false;

}
