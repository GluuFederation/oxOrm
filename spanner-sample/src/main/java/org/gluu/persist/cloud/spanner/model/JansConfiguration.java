/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.cloud.spanner.model;

import java.io.Serializable;
import java.util.List;

import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DataEntry;
import org.gluu.persist.annotation.JsonObject;
import org.gluu.persist.annotation.ObjectClass;
import org.gluu.persist.model.base.InumEntry;

/**
 * Gluu Configuration
 *
 * @author Yuriy Movchan Date: 08.27.2012
 */
@DataEntry
@ObjectClass(value = "jansAppConf")
public class JansConfiguration extends InumEntry implements Serializable {

	private static final long serialVersionUID = -2818003894646725601L;

	@AttributeName(ignoreDuringUpdate = true)
	private String inum;

	@AttributeName(name = "jansDbAuth")
	@JsonObject
	private List<IDPAuthConf> idpAuthn;

	@AttributeName(name = "jansAuthMode")
	private String authenticationMode;

	public String getInum() {
		return inum;
	}

	public void setInum(String inum) {
		this.inum = inum;
	}

	public List<IDPAuthConf> getIdpAuthn() {
		return idpAuthn;
	}

	public void setIdpAuthn(List<IDPAuthConf> idpAuthn) {
		this.idpAuthn = idpAuthn;
	}

	public String getAuthenticationMode() {
		return authenticationMode;
	}

	public void setAuthenticationMode(String authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

}
