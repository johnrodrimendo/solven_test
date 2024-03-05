package com.affirm.wavy.model;

public class HSMMessage {

    private long ttl;
    private HSM hsm;

    public HSMMessage() {
        this.ttl = 7;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public HSM getHsm() {
        return hsm;
    }

    public void setHsm(HSM hsm) {
        this.hsm = hsm;
    }
}
