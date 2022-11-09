/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.exception;

/**
 * An exception is a result if search operation fails
 *
 * @author Yuriy Movchan Date: 2022/11/04
 */
public class SearchEntryException extends BasePersistenceException {

    private static final long serialVersionUID = 1321766232087075304L;

    public SearchEntryException(Throwable root) {
        super(root);
    }

    public SearchEntryException(String string, Throwable root) {
        super(string, root);
    }

    public SearchEntryException(String s) {
        super(s);
    }

}
