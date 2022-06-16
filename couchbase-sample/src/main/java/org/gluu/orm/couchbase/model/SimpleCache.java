package org.gluu.orm.couchbase.model;

import java.util.Date;

import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DataEntry;
import org.gluu.persist.annotation.Expiration;
import org.gluu.persist.annotation.ObjectClass;
import org.gluu.persist.model.base.BaseEntry;

@DataEntry
@ObjectClass(value = "cache")
public class SimpleCache extends BaseEntry {

    @Expiration
    private Integer ttl;
    @AttributeName(name = "uuid")
    private String id;
    @AttributeName(name = "iat")
    private Date creationDate;
    @AttributeName(name = "dat")
    private String data;

    @AttributeName(name = "exp", consistency = true)
    private Date expirationDate;
    @AttributeName(name = "del")
    private Boolean deletable;

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean getDeletable() {
		return deletable;
	}

	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}

	@Override
	public String toString() {
		return "SimpleCache [ttl=" + ttl + ", id=" + id + ", creationDate=" + creationDate + ", data=" + data
				+ ", expirationDate=" + expirationDate + ", deletable=" + deletable + "]";
	}
}
