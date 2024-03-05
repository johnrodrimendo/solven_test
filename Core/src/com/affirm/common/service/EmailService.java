package com.affirm.common.service;

import com.affirm.common.model.transactional.Attachment;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by john on 21/10/16.
 */
public interface EmailService {

    void sendPlainMail(String from, String fromName, String to, String[] cc, String subject, String message, String templateId, List<Attachment> attached, Map<String, String> customArgs, String hourOfDay);

    void sendHtmlMail(String from, String fromName, String to, String[] cc, String subject, String message, String templateId, List<Attachment> attached, Map<String, String> customArgs, String hourOfDay, JSONObject jsonVariables);
}
