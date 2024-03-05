package com.affirm.client.controller;

import com.affirm.common.dao.ExternalWSRecordDAO;
import com.affirm.common.dao.InteractionDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.ApprovalValidation;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.MatiResult;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.LoanApplicationApprovalValidationService;
import com.affirm.common.service.PagoEfectivoClientService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.mati.service.MatiService;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.system.configuration.Configuration;
import com.affirm.warmi.model.ProcessDetail;
import com.affirm.warmi.service.WarmiService;
import com.affirm.wavy.model.ReceivedMessage;
import com.affirm.wavy.model.ReceivedMessageChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class WebHookController {

    private static Logger logger = Logger.getLogger(WebHookController.class);

    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WarmiService warmiService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private MatiService matiService;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;
    @Autowired
    private PagoEfectivoClientService pagoEfectivoClientService;
    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;

    //TODO Validate by IP or soemthing so anyone except the services can access these controllers!

    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/sendgrid", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> sendgridWebhook(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        System.out.println("Data: " + data);

        JSONArray array = new JSONArray(data);
        for (int i = 0; i < array.length(); i++) {
            Timestamp timestamp = new Timestamp(array.getJSONObject(i).getLong("timestamp") * 1000);
            String event = array.getJSONObject(i).getString("event");
            Integer id = JsonUtil.getIntFromJson(array.getJSONObject(i), "interaction_id", null);
            if (id != null)
                interactionDao.insertPersonInteractionStatus(id, event, timestamp);
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/infobip/{idInteraction}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> infobipWebhook(@PathVariable Integer idInteraction,
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        JSONObject jsonObject = new JSONObject(data);
        System.out.println("data: " + data);
        JSONArray array = jsonObject.getJSONArray("results");

        for (int i = 0; i < array.length(); i++) {
            String doneAt = array.getJSONObject(i).getString("doneAt");
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(doneAt);
            Timestamp timestamp = new Timestamp(date.getTime());
            String status = array.getJSONObject(i).getJSONObject("status").getString("groupName");
            if (idInteraction != null)
                interactionDao.insertPersonInteractionStatus(idInteraction, status, timestamp);
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/twilio/{personInteractionId}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> twilioWebhook(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("personInteractionId") Integer personInteractionId) throws Exception {

        System.out.println("Entrando a tiwiloWebhook");
        Map<String, String[]> parameters = request.getParameterMap();

        for (String key : parameters.keySet()) {
            String[] vals = parameters.get(key);
            for (String val : vals)
                System.out.println(key + " -> " + val);
        }

        interactionDao.insertPersonInteractionStatus(personInteractionId, request.getParameter("MessageStatus"), new Timestamp(System.currentTimeMillis()));
        return AjaxResponse.ok(null);
    }

    @CrossOrigin
    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/twilio/voicecall/{personInteractionId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> twilioVoiceCallXML(
            @PathVariable("personInteractionId") String encodedInteractionId, Locale locale) throws Exception {

        Integer personInteractionId = Integer.parseInt(CryptoUtil.decrypt(encodedInteractionId));

        PersonInteraction personInteraction = interactionDao.getPersonInteractionById(personInteractionId, locale);

        String body = personInteraction.getBody();
        TwiMLResponse twiml = new TwiMLResponse();
        try {
            Say message = new Say(body);
            message.setVoice("alice");
            message.setLanguage("es-ES");
            message.setLoop(10);
            twiml.append(message);
        } catch (TwiMLException e) {
            logger.error("Error with Twilio XML", e);
        }
        return ResponseEntity.ok(twiml.toXML());
    }

    @CrossOrigin
    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/wavy/messages", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> wavyMessagesReceived(@RequestBody String jsonData) {
        try {
            JSONObject json = new JSONObject(jsonData);
            List<ReceivedMessage> receivedMessageList = new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "data", null).toString(), new TypeToken<List<ReceivedMessage>>() {
            }.getType());

            for (ReceivedMessage message : receivedMessageList) {
                Timestamp timestamp = new Timestamp(message.getReceivedDate().getTime());

                String receivedMessage;
                if (ReceivedMessageChat.MessageType.TEXT.toString().equals(message.getMessage().getType())) {
                    receivedMessage = message.getMessage().getMessageText();
                } else if (ReceivedMessageChat.MessageType.AUDIO.name().equals(message.getMessage().getType()) || ReceivedMessageChat.MessageType.IMAGE.name().equals(message.getMessage().getType())) {
                    receivedMessage = message.getMessage().getMediaUrl();
                } else {
                    receivedMessage = null;
                }

                interactionDao.insertPersonInteractionResponse(Integer.parseInt(message.getCorrelationId()), timestamp, receivedMessage, new JSONObject(message));
            }

            return AjaxResponse.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage(e.getMessage());
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/wavy/status", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> wavyMessagesStatus(@RequestBody String jsonData) {
        try {
//            TODO: STORE WAVY MESSAGES STATUS???
            logger.debug(jsonData);
            return AjaxResponse.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage(e.getMessage());
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/warmi", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> warmiWebhook(@RequestBody ProcessDetail result) {
        try {
            logger.debug(new Gson().toJson(result));
            warmiService.saveResult(result);

            return AjaxResponse.ok(null);
        } catch (Exception e) {
            logger.error("Exception", e);
            return AjaxResponse.errorMessage(e.getMessage());
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/mati", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> matiWebhook(@RequestBody String body) {
        try {
            logger.debug(body);
            JSONObject json = new JSONObject(body);
            String eventName = json.getString("eventName");
            if(eventName.equalsIgnoreCase("verification_completed")){
                JSONObject jsonMeta = json.getJSONObject("metadata");
                List<MatiResult> matiResults = securityDAO.getMatiResultsByLoanApplication(jsonMeta.getInt("loanId"));
                MatiResult matiResult = matiResults.stream().filter(m -> m.getId() == jsonMeta.getInt("matiResultId")).findFirst().orElse(null);
                if(matiResult != null){
                    String token = matiService.getOauthToken();
                    String result = matiService.getVerification(matiResult.getMatiVerificationId(), token);
                    securityDAO.updateMatiResult(matiResult.getId(), new Date(), result, matiResult.getStatus());
                    loanApplicationApprovalValidationService.validateAndUpdate(jsonMeta.getInt("loanId"), ApprovalValidation.IDENTIDAD);
                    matiResult.setMatiResponse(result);
                    // If the person doesnt has gender, update it with the response of Mati
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonMeta.getInt("loanId"), Configuration.getDefaultLocale());
                    Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
                    String matiResultGender = matiResult.getGender();
                    if(person.getGender() == null && matiResultGender != null && !matiResultGender.isEmpty())
                        personDao.updateGender(person.getId(), matiResultGender.charAt(0));
                }
            }

            return AjaxResponse.ok(null);
        } catch (Exception e) {
            errorService.onError(e);
            return AjaxResponse.errorMessage(e.getMessage());
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/pagoefectivo", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> pagoEfectivoWebhook(@RequestBody String result, @RequestHeader("PE-Signature") String signature) throws Exception {
        try {
            logger.debug(result);
            externalWSRecordDAO.insertWebhookRequest("/"+Configuration.WEBHOOK_PATH+"/pagoefectivo", result);
            pagoEfectivoClientService.validateWebhookData(result,signature);
            pagoEfectivoClientService.CIPPayed(result);

            return AjaxResponse.ok(null);
        } catch (Exception e) {
            logger.error("Exception", e);
            if(result != null) {
                errorService.sendErrorCriticSlack(String.format("ERROR PAGO EFECTIVO WEBHOOK: \n Mensaje: ERROR al procesar \n Body: %s", result));
                externalWSRecordDAO.insertWebhookRequest("/"+Configuration.WEBHOOK_PATH+"/pagoefectivo",result);
            }
            return AjaxResponse.errorMessage(e.getMessage());
        }
    }
}
