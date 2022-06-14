/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.hybrid.impl;

import java.util.List;

import org.gluu.persist.exception.AuthenticationException;
import org.gluu.persist.exception.operation.ConnectionException;
import org.gluu.persist.exception.operation.SearchException;
import org.gluu.persist.extension.PersistenceExtension;
import org.gluu.persist.operation.PersistenceOperationService;

/**
 * Hybrid Operation Service
 *
 * @author Yuriy Movchan Date: 05/13/2018
 */
public class HybridPersistenceOperationService implements PersistenceOperationService {

	private List<PersistenceOperationService> persistenceOperationServices;

	public HybridPersistenceOperationService(List<PersistenceOperationService> persistenceOperationServices) {
		this.persistenceOperationServices = persistenceOperationServices;
	}

	@Override
	public boolean isConnected() {
		for(PersistenceOperationService persistenceOperationService : persistenceOperationServices) {
			if (!persistenceOperationService.isConnected()) {
				return false;
			}
		}

		return true;
	}

	public List<PersistenceOperationService> getPersistenceOperationServices() {
		return persistenceOperationServices;
	}

	@Override
	public void setPersistenceExtension(PersistenceExtension persistenceExtension) {
		for(PersistenceOperationService persistenceOperationService : persistenceOperationServices) {
			persistenceOperationService.setPersistenceExtension(persistenceExtension);
		}
	}

	@Override
	public boolean authenticate(String key, String password, String objectClass) throws ConnectionException, SearchException, AuthenticationException {
        throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean isSupportObjectClass(String objectClass) {
        throw new UnsupportedOperationException("Method not implemented.");
	}

}
