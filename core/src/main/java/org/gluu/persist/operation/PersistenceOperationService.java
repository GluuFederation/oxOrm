/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.operation;

import org.gluu.persist.exception.AuthenticationException;
import org.gluu.persist.exception.operation.ConnectionException;
import org.gluu.persist.exception.operation.EntryConvertationException;
import org.gluu.persist.exception.operation.SearchException;
import org.gluu.persist.extension.PersistenceExtension;

/**
 * Base interface for Operation Service
 *
 * @author Yuriy Movchan Date: 06/22/2018
 */
public interface PersistenceOperationService {

	boolean isConnected();

    boolean authenticate(String key, String password, String objectClass) throws ConnectionException, SearchException, AuthenticationException, EntryConvertationException;

	public void setPersistenceExtension(PersistenceExtension persistenceExtension);

	public boolean isSupportObjectClass(String objectClass);

}
