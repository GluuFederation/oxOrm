/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.ldap.impl;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.ArrayList;
import java.util.List;

import org.gluu.persist.model.BatchOperation;
import org.gluu.persist.reflect.property.PropertyAnnotation;

/**
 * LDAP batch operation wrapper
 *
 * @author Yuriy Movchan Date: 02/07/2010
 */
public class LdapBatchOperationWraper<T> {

    private LdapEntryManager ldapEntryManager;
    private Class<T> entryClass;
    private List<PropertyAnnotation> propertiesAnnotations;

    private BatchOperation<T> batchOperation;

    public LdapBatchOperationWraper(BatchOperation<T> batchOperation) {
        this.batchOperation = batchOperation;
    }

    public LdapBatchOperationWraper(BatchOperation<T> batchOperation, LdapEntryManager ldapEntryManager, Class<T> entryClass,
            List<PropertyAnnotation> propertiesAnnotations) {
        this.batchOperation = batchOperation;
        this.ldapEntryManager = ldapEntryManager;
        this.entryClass = entryClass;
        this.propertiesAnnotations = propertiesAnnotations;
    }

    public final BatchOperation<T> getBatchOperation() {
        return batchOperation;
    }

    public List<T> createEntities(SearchResult searchResult) {
        if (ldapEntryManager == null) {
            return new ArrayList<T>(0);
        }
        SearchResultEntry[] searchResultEntry = searchResult.getSearchEntries()
                .toArray(new SearchResultEntry[searchResult.getSearchEntries().size()]);

        return ldapEntryManager.createEntities(entryClass, propertiesAnnotations, searchResultEntry);
    }

}
