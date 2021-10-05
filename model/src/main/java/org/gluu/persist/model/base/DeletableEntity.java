/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.model.base;

import java.util.Date;

import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DataEntry;

/**
 * @author Yuriy Zabrovarnyy
 */
@DataEntry
public class DeletableEntity extends BaseEntry implements Deletable {

    @AttributeName(name = "exp")
    private Date expirationDate;
    @AttributeName(name = "del")
    private Boolean deletable;

    @Override
    public Boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

	public boolean canDelete() {
        return canDelete(new Date());
    }

    public boolean canDelete(Date now) {
    	Date exp = expirationDate != null ? expirationDate :  null;
        return deletable != null && deletable && (exp == null || exp.before(now));
    }

    @Override
    public String toString() {
    	Date exp = expirationDate != null ? expirationDate :  null;
        return "DeletableEntity{" +
                "expirationDate=" + exp +
                ", deletable=" + deletable +
                "} " + super.toString();
    }
}
