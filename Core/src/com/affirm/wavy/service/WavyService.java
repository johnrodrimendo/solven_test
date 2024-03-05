package com.affirm.wavy.service;

import com.affirm.wavy.model.Destination;
import com.affirm.wavy.model.HSMMessage;
import com.affirm.wavy.model.Message;

import java.util.List;

public interface WavyService {

    void sendTextMessage(List<Destination> destinations, Message message, String campaignAlias) throws Exception;

    void sendTextMessage(List<Destination> destinations, Message message, String campaignAlias, String defaultExtraInfo) throws Exception;

    void sendHSMMessage(List<Destination> destinations, HSMMessage message, String campaignAlias) throws Exception;

    void sendHSMMessage(List<Destination> destinations, HSMMessage message, String campaignAlias, String defaultExtraInfo) throws Exception;

}
