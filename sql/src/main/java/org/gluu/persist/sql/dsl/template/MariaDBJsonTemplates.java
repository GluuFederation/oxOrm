package org.gluu.persist.sql.dsl.template;

import org.gluu.persist.sql.impl.SqlOps;

import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;

/**
 * MySQL DSL templates for JSON support
 *
 * @author Yuriy Movchan Date: 01/27/2021
 */
public class MariaDBJsonTemplates extends MySQLTemplates {
	
    public static Builder builder() {
        return new Builder() {
            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new MariaDBJsonTemplates(escape, quote);
            }
        };
    }

    public MariaDBJsonTemplates(char escape, boolean quote) {
		super(escape, quote);

		add(SqlOps.JSON_CONTAINS, "JSON_CONTAINS({0}, {1}, {2})");
		add(SqlOps.JSON_EXTRACT, "JSON_EXTRACT({0}, {1})");
	}

}