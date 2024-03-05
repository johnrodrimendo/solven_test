package com.affirm.ripley;

import com.affirm.ripley.model.RipleyPreAprrovedBase;
import com.affirm.ripley.service.Impl.RipleyProcessImpl;

/**
 * Created by miberico on 25/05/17.
 */
public class MainRipley {

    public static void main(String [] args) {
        RipleyPreAprrovedBase base = new RipleyPreAprrovedBase();
        RipleyProcessImpl process = new RipleyProcessImpl();
        process.processRipley(base, 18 , 4000);
    }
}
