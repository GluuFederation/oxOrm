package org.gluu.model.custom.script.type.spontaneous;

import org.gluu.model.custom.script.type.BaseExternalType;

public interface SpontaneousScopeType extends BaseExternalType {

    boolean allowScope(Object context);
}
