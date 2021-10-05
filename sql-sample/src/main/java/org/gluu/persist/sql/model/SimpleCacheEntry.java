package org.gluu.persist.sql.model;

import java.io.Serializable;
import java.util.Date;

import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DN;
import org.gluu.persist.annotation.DataEntry;
import org.gluu.persist.annotation.Expiration;
import org.gluu.persist.annotation.ObjectClass;
import org.gluu.persist.model.base.Deletable;
import org.gluu.persist.model.base.DeletableEntity;

@DataEntry(forceUpdate = true)
@ObjectClass(value = "gluuCache")
public class SimpleCacheEntry extends DeletableEntity implements Serializable, Deletable {

	private static final long serialVersionUID = 3360900373193184522L;

	@DN
    private String dn;
    @Expiration
    private Integer ttl;
    @AttributeName(name = "uuid")
    private String id;
    @AttributeName(name = "iat")
    private Date creationDate;
    @AttributeName(name = "dat")
    private String data;

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

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

    @Override
	public String toString() {
		return "NativePersistenceCacheEntity [dn=" + dn + ", ttl=" + ttl + ", id=" + id + ", creationDate=" + creationDate + ", data="
				+ data + "]";
	}
}
