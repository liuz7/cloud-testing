package com.vmware.vchs.gateway.model.annotation;

public enum UpdateType {
    /**
     * only need update gateway's meta data.
     */
    LOCAL,

    /**
     * need evoke remote call.
     */
    REMOTE,

    /**
     * not supported yet.
     */
    NOT_IMPLEMENTED,

    /**
     * new value will be ignored.
     */
    IGNORE,
}
