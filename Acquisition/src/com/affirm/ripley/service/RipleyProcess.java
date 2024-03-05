package com.affirm.ripley.service;

import com.affirm.ripley.model.RipleyPreAprrovedBase;

/**
 * Created by miberico on 25/05/17.
 */
public interface RipleyProcess {

    void processRipley(RipleyPreAprrovedBase cliente, int clientPlazo, double clientAmount);

}
