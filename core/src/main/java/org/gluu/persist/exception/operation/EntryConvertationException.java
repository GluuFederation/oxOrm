/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.exception.operation;

/**
 * Failed to convert DB result to entry 
 *
 * @author Yuriy Movchan Date: 01/18/2021
 */
public class EntryConvertationException extends PersistenceException {

    private static final long serialVersionUID = 1321766232087075304L;

    public EntryConvertationException(Throwable root) {
        super(root);
    }

    public EntryConvertationException(String string, Throwable root) {
        super(string, root);
    }

    public EntryConvertationException(String s) {
        super(s);
    }

}
