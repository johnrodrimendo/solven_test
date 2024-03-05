package com.affirm.acquisition.controller;

import com.affirm.client.model.form.ContactForm;
import com.affirm.client.model.form.ProcessContactForm;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionCategory;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.onesignal.service.OneSignalService;
import com.affirm.onesignal.service.impl.OneSignalServiceImpl;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

@Controller
@Scope("request")
public class RootController {

    private static Logger logger = Logger.getLogger(RootController.class);

    private final LoanApplicationDAO loanApplicationDAO;
    private final OneSignalService oneSignalService;

    public RootController(LoanApplicationDAO loanApplicationDAO, OneSignalService oneSignalService) {
        this.loanApplicationDAO = loanApplicationDAO;
        this.oneSignalService = oneSignalService;
    }

    @RequestMapping(value = "/chooseAgent", method = RequestMethod.GET)
    public String chooseAgent(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        return "/loanApplication/formQuestions/chooseAgent";
    }

    @RequestMapping(value = "/postSignature", method = RequestMethod.GET)
    public String postSinature(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        return "/loanApplication/formQuestions/postSignature";
    }


    @RequestMapping(value = "/consolidacion", method = RequestMethod.GET)
    public String consolidation(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        ContactForm contactForm = new ContactForm();
        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("contactForm", contactForm);
        return "/loanApplication/formQuestions/consolidationOffer";
    }

    @RequestMapping(value = "/loanapplication/updatePlayerId", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> saveOneSignalLastActiveBrowser(
            Locale locale, HttpServletRequest request,
            @RequestBody String jsonBody
    ) throws Exception {
        JSONObject json = new JSONObject(jsonBody);
        logger.debug(json.toString());
        String token = json.optString("token", null);
        String playerId = json.optString("playerId", null);
        String decryptedToken = CryptoUtil.decrypt(token);

        if (decryptedToken != null) {
            Integer loanApplicationId = JsonUtil.getIntFromJson(new JSONObject(decryptedToken), "loan", null);

            String oneSignalJSONActive = "active";
            String oneSignalJSONHistory = "history";
            String oneSignalJSONScheduledNotificationId = "scheduled_notification_id";

            if (loanApplicationId != null) {
                LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                Integer countryId = loanApplication.getCountryId();
                JSONObject tokens = loanApplication.getJsNotificationTokens() == null ? new JSONObject() : loanApplication.getJsNotificationTokens();

//                GUARDAR TOKEN EN LOANAPPLICATION
                JSONObject oneSignalTokens = tokens.has(OneSignalServiceImpl.ONESIGNAL_PROVIDER) ? tokens.getJSONObject(OneSignalServiceImpl.ONESIGNAL_PROVIDER) : new JSONObject();
                oneSignalTokens.put(oneSignalJSONActive, playerId);

                if ((oneSignalTokens.has(oneSignalJSONHistory) && !oneSignalTokens.getJSONArray(oneSignalJSONHistory).toList().contains(playerId)) && playerId != null) {
                    oneSignalTokens.getJSONArray(oneSignalJSONHistory).put(playerId);
                } else if (!oneSignalTokens.has(oneSignalJSONHistory) && playerId != null) {
                    oneSignalTokens.put(oneSignalJSONHistory, Collections.singletonList(playerId));
                }

//                SI YA CONTABA CON UNA NOTIFICACION A FUTURO. ELIMINO REGISTRO Y CANCELO NOTIFICACION
                if (oneSignalTokens.has(oneSignalJSONScheduledNotificationId)) {
                    boolean canceledNotification = oneSignalService.cancelScheduledNotification(loanApplication.getEntityId(), countryId, oneSignalTokens.getString(oneSignalJSONScheduledNotificationId));
                    logger.debug((canceledNotification ? "Scheduled notification canceled with id: " : "Could not cancel scheduled notification with id: ") + oneSignalTokens.getString(oneSignalJSONScheduledNotificationId));
                    oneSignalTokens.remove(oneSignalJSONScheduledNotificationId);
                }

//                ENVIAR NOTIFICACION DENTRO DE X MINUTOS
                if (playerId != null) {
                    String[] notificationMessage;
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.OFFER.id) {
                        notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.WITH_OFFER);
                    } else if (loanApplication.getCurrentProcessQuestion().getCategory().getId() == ProcessQuestionCategory.VERIFICATION) {
                        notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.WITH_ACCEPTED_OFFER);
                    } else {
                        notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.WITH_OFFER);
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MINUTE, 3);

                    String timeToSendNotification = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0));

                    JSONObject filters = oneSignalService.applyNotificationsScheduleDelivery(null, timeToSendNotification, null);
                    String notificationId = oneSignalService.sendNotification(loanApplication.getEntityId(), countryId, notificationMessage, Collections.singletonList(playerId), null, filters);
                    oneSignalTokens.put(oneSignalJSONScheduledNotificationId, notificationId);
                }

                logger.debug(oneSignalTokens);
                tokens.put(OneSignalServiceImpl.ONESIGNAL_PROVIDER, oneSignalTokens);

                loanApplicationDAO.saveLastActiveOneSignalPlayerId(loanApplicationId, tokens);

            } else {
                logger.error("Decrypted token has no loan property");
            }
        } else {
            logger.warn("Could not decrypt token");
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanapplication/{loanAplicationToken}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLoanApplicationStatus(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        // Redirect depending the product
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        return new ModelAndView("redirect:/" + ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" + Configuration.EVALUATION_CONTROLLER_URL + "/" + loanAplicationToken);
    }
}
