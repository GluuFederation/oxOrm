package org.gluu.persist.sql.dsl.template;

import org.gluu.persist.sql.dsl.types.PostgreSQLJsonType;

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
	}

}