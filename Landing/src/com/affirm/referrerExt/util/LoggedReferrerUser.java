package com.affirm.referrerExt.util;

import com.affirm.common.util.*;
import com.affirm.referrerExt.model.ReferrerUser;

import java.io.Serializable;

public class LoggedReferrerUser implements Serializable {

    private ReferrerUser referrerUser;

    public ReferrerUser getReferrerUser() {
        return referrerUser;
    }

    public void setReferrerUser(ReferrerUser referrerUser) {
        this.referrerUser = referrerUser;
    }
}
