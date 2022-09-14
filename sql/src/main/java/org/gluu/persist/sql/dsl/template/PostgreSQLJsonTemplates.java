package org.gluu.persist.sql.dsl.template;

import org.gluu.persist.sql.dsl.types.PostgreSQLJsonType;
import org.gluu.persist.sql.impl.SqlOps;

import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLTemplates;

/**
 * PostgreSQL DSL templates for JSON support
 *
 * @author Yuriy Movchan Date: 09/01/2022
 */
public class PostgreSQLJsonTemplates extends PostgreSQLTemplates {
	
    public static Builder builder() {
        return new Builder() {
            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new PostgreSQLJsonTemplates(escape, quote);
            }
        };
    }

    public PostgreSQLJsonTemplates(char escape, boolean quote) {
		super(escape, quote);
		
		addCustomType(new PostgreSQLJsonType());

		add(SqlOps.PGSQL_JSON_CONTAINS, "{0} @> {1}::jsonb");
//		add(SqlOps.PGSQL_JSON_PATH_QUERY_ARRAY, "jsonb_array_length(jsonb_path_query_array({0}, CONCAT('$[*] ? (@', {1}, {2}, ')')::jsonpath)) > 0");
		add(SqlOps.PGSQL_JSON_PATH_QUERY_ARRAY, "jsonb_path_query_array({0}, CONCAT('$[*] ? (@', {1}, {2}, ')')::jsonpath)");
		
		add(SqlOps.PGSQL_JSON_NOT_EMPTY_ARRAY, "jsonb_array_length({0}) > 0");
	}

}