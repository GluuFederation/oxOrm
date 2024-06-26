/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.persist.model.fido2;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DataEntry;
import org.gluu.persist.annotation.Expiration;
import org.gluu.persist.annotation.ObjectClass;
import org.gluu.persist.model.base.BaseEntry;

/**
 * Fido2 base persistence entry
 *
 * @author Yuriy Movchan
 * @version 11/02/2018
 */
@DataEntry(sortBy = "creationDate")
@ObjectClass
public class Fido2Entry extends BaseEntry {

	private static final long serialVersionUID = -3122430771066187529L;

    @AttributeName(ignoreDuringUpdate = true, name = "oxId")
    private String id;

    @AttributeName(name = "oxCodeChallenge")
    private String challange;

    @AttributeName(name = "oxCodeChallengeHash")
    private Integer challengeHash;

    @AttributeName(name = "creationDate")
    private Date creationDate;

    @AttributeName(name = "personInum")
    private String userInum;

	@AttributeName(name = "oxApplication")
	private String rpId;

    @AttributeName(name = "oxSessionStateId")
    private String sessionStateId;

    @AttributeName(name = "exp")
    private Date expirationDate;

    @AttributeName(name = "del")
    private boolean deletable = true;

    @Expiration
    private Integer ttl;

    public Fido2Entry() {
    }

    public Fido2Entry(String dn) {
        super(dn);
    }

    public Fido2Entry(String dn, String id, Date creationDate, String userInum, String challange) {
        super(dn);
        this.id = id;
        this.creationDate = creationDate;
        this.userInum = userInum;
        this.challange = challange;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChallange() {
        return challange;
    }

    public void setChallange(String challange) {
        this.challange = challange;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserInum() {
        return userInum;
    }

    public void setUserInum(String userInum) {
        this.userInum = userInum;
    }

	public String getRpId() {
		return rpId;
	}

	public void setRpId(String rpId) {
		this.rpId = rpId;
	}

	public String getSessionStateId() {
		return sessionStateId;
	}

	public void setSessionStateId(String sessionStateId) {
		this.sessionStateId = sessionStateId;
	}

	public Integer getChallengeHash() {
		return challengeHash;
	}

	public void setChallengeHash(Integer challengeHash) {
		this.challengeHash = challengeHash;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	public void clearExpiration() {
        this.expirationDate = null;
        this.deletable = false;
        this.ttl = 0;
	}

	public void setExpiration(int expirationInSeconds) {
        if (creationDate != null) {
            final int expiration = expirationInSeconds;
            Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            calendar.setTime(creationDate);
            calendar.add(Calendar.SECOND, expiration);
            this.expirationDate = calendar.getTime();
            this.deletable = true;
            this.ttl = expiration;
        }
    }

}
