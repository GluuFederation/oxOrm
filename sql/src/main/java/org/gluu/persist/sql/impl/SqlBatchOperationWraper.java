/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql.impl;

import java.util.ArrayList;
import java.util.List;

import org.gluu.persist.model.BatchOperation;
import org.gluu.persist.model.EntryData;
import org.gluu.persist.reflect.property.PropertyAnnotation;

/**
 * SQL batch operation wrapper
 *
 * @author Yuriy Movchan Date: 01/10/2020
 */
public class SqlBatchOperationWraper<T> {

    private SqlEntryManager SqlEntryManager;
    private Class<T> entryClass;
    private List<PropertyAnnotation> propertiesAnnotations;

    private BatchOperation<T> batchOperation;

    public SqlBatchOperationWraper(BatchOperation<T> batchOperation) {
        this.batchOperation = batchOperation;
    }

    public SqlBatchOperationWraper(BatchOperation<T> batchOperation, SqlEntryManager SqlEntryManager, Class<T> entryClass,
            List<PropertyAnnotation> propertiesAnnotations) {
        this.batchOperation = batchOperation;
        this.SqlEntryManager = SqlEntryManager;
        this.entryClass = entryClass;
        this.propertiesAnnotations = propertiesAnnotations;
    }

    public final BatchOperation<T> getBatchOperation() {
        return batchOperation;
    }

    public List<T> createEntities(List<EntryData> entryDataList) {
        if (SqlEntryManager == null) {
            return new ArrayList<T>(0);
        }

        return SqlEntryManager.createEntities(entryClass, propertiesAnnotations, null, entryDataList.toArray(new EntryData[entryDataList.size()]));
    }

}
