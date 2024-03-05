package com.affirm.security.model;

/**
 * Created by john on 23/08/17.
 */
public interface ITwoFactorAuthLoggedUser {

    boolean need2FA();

    boolean is2FALogged();

    void set2FALogged();

    String get2FASharedSecret();
}
