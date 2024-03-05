package com.affirm.client.service;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.profile.FbProfile;


/**
 * Created by jarmando on 01/12/16.
 */
public interface MessengerSession {
    void clean(String senderId);
    void cleanOld();
    void cleanEverything();
    SessionData initSessionData(String senderId, FbProfile fbProfile, String initState);
    SessionData getSessionData(String senderId);
    void override(SessionData sessionData);
}
/*--Greeting message
curl -X POST -H "Content-Type: application/json" -d '{
  "setting_type":"greeting",
  "greeting":{
    "text":"Hola {{user_first_name}}, en Solven obtendrás el crédito que mereces."
  }
}' "https://graph.facebook.com/v2.6/me/thread_settings?access_token="

--PERSISTENT MENU
curl -X POST -H "Content-Type: application/json" -d '{
  "setting_type" : "call_to_actions",
  "thread_state" : "existing_thread",
  "call_to_actions":[
    {
      "type":"postback",
      "title":"Pedir un producto",
      "payload":"_product_"
    },
    {
      "type":"web_url",
      "title":"Página web",
      "url":"http://www.solven.pe/"
    },
    {
      "type":"postback",
      "title":"Dejar un mensaje",
      "payload":"_contact_"
    }
  ]
}' "https://graph.facebook.com/v2.6/me/thread_settings?access_token="*/