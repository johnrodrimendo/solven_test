package com.affirm.client.model.messengerbot.utils;

import com.affirm.client.service.MessengerSession;

/**
 * Created by jarmando on 04/12/16.
 * The idea is that he handles locale key resolution and the building blocks of any answer on FbHandler
 * methods should input state and data only. method names should be meaninful and locale keys are encapsulated here
 * along with message building.
 */
public class PreLoanAppHandler {
    MessengerSession messengerSession;

    public PreLoanAppHandler(MessengerSession messengerSession){
        this.messengerSession = messengerSession;
    }

}
