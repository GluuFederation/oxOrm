/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql.operation;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.List;

import org.gluu.persist.exception.operation.DeleteException;
import org.gluu.persist.exception.operation.DuplicateEntryException;
import org.gluu.persist.exception.operation.EntryConvertationException;
import org.gluu.persist.exception.operation.EntryNotFoundException;
import org.gluu.persist.exception.operation.PersistenceException;
import org.gluu.persist.exception.operation.SearchException;
import org.gluu.persist.model.AttributeData;
import org.gluu.persist.model.AttributeDataModification;
import org.gluu.persist.model.EntryData;
import org.gluu.persist.model.PagedResult;
import org.gluu.persist.model.SearchScope;
import org.gluu.persist.operation.PersistenceOperationService;
import org.gluu.persist.sql.impl.SqlBatchOperationWraper;
import org.gluu.persist.sql.model.ConvertedExpression;
import org.gluu.persist.sql.model.SearchReturnDataType;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;

import com.querydsl.core.types.OrderSpecifier;

/**
 * SQL operation service interface
 *
 * @author Yuriy Movchan Date: 12/22/2020
 */
public interface SqlOperationService extends PersistenceOperationService {

    static String DN = "dn";
    static String UID = "uid";
    static String[] UID_ARRAY = new String[] { "uid" };
    static String USER_PASSWORD = "userPassword";
    static String OBJECT_CLASS = "objectClass";

    static String DOC_ALIAS = "doc";
    static String ID = "id";
    static String DOC_ID = "doc_id";

	public static final String SQL_DATA_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final Object[] NO_OBJECTS = new Object[0];

    SqlConnectionProvider getConnectionProvider();

    boolean addEntry(String key, String objectClass, Collection<AttributeData> attributes) throws DuplicateEntryException, PersistenceException;

    boolean updateEntry(String key, String objectClass, List<AttributeDataModification> mods) throws UnsupportedOperationException, PersistenceException;

    boolean delete(String key, String objectClass) throws EntryNotFoundException;
	long delete(String key, String objectClass, ConvertedExpression expression, int count) throws DeleteException;

	boolean deleteRecursively(String key, String objectClass) throws EntryNotFoundException, SearchException;

	List<AttributeData> lookup(String key, String objectClass, String... attributes) throws SearchException, EntryConvertationException;

    <O> PagedResult<EntryData> search(String key, String objectClass, ConvertedExpression expression, SearchScope scope,
            String[] attributes, OrderSpecifier<?>[] orderBy, SqlBatchOperationWraper<O> batchOperationWraper, SearchReturnDataType returnDataType,
            int start, int count, int pageSize) throws SearchException;

    String[] createStoragePassword(String[] passwords);
    
    boolean isBinaryAttribute(String attribute);
    boolean isCertificateAttribute(String attribute);

    String escapeValue(String value);
	void escapeValues(Object[] realValues);

	String unescapeValue(String value);
	void unescapeValues(Object[] realValues);

	String toInternalAttribute(String attributeName);
	String[] toInternalAttributes(String[] attributeNames);

	String fromInternalAttribute(String internalAttributeName);
	String[] fromInternalAttributes(String[] internalAttributeNames);

    boolean destroy();

	Connection getConnection();

	DatabaseMetaData getMetadata();

	boolean isJsonColumn(String tableName, String attributeType);

}
