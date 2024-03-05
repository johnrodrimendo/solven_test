package com.affirm.dialogflow;

import com.affirm.common.service.ErrorService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/chatbot")
public class DialogflowController {

    private static final Logger logger = Logger.getLogger(DialogflowController.class);

    private static final ExecutorService createLoanExecutor = Executors.newFixedThreadPool(5);

    private final DialogflowService dialogflowService;
    private final ErrorService errorService;
    private final MessageSource messageSource;

    public DialogflowController(DialogflowService dialogflowService, ErrorService errorService, MessageSource messageSource) {
        this.dialogflowService = dialogflowService;
        this.errorService = errorService;
        this.messageSource = messageSource;
    }

    @CrossOrigin
    @PostMapping(value = "/activeLoanApplication", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity validateUserHasActiveLoanApplication(@RequestBody DialogflowRequest data) {
        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data);

            return AjaxResponse.ok(null);
        } catch (Throwable throwable) {
            return defaultCatch(throwable);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/registerLoan", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> registerLoanApplication(@RequestBody DialogflowRequest data) {
        logger.info(data.toString());

        try {
            dialogflowService.preValidateUserHasNotActiveLoanApplication(data);

            createLoanExecutor.execute(() -> {
                try {
                    dialogflowService.registerFirstInstanceLoanApplication(data);
                } catch (Throwable throwable) {
                    defaultCatch(throwable);
                }
            });
            return AjaxResponse.ok(null);
        } catch (Throwable throwable) {
            return defaultCatch(throwable);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/updateEmail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateEmail(@RequestBody DialogflowRequest data) {
        try {
            dialogflowService.updateLoanApplicationEmail(data);

            return AjaxResponse.ok(null);
        } catch (Throwable throwable) {
            return defaultCatch(throwable);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/updateCellphone", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateCellphoneAndReturnActiveLoanApplication(@RequestBody DialogflowRequest data) {
        try {
            JSONObject jsonResponse = new JSONObject();
            String activeLoanApplicationURL = dialogflowService.updateLoanApplicationPhoneAndReturnLink(data);
            jsonResponse.put("link", activeLoanApplicationURL);
            logger.info(activeLoanApplicationURL);

            return AjaxResponse.ok(jsonResponse.toString());
        } catch (Throwable throwable) {
            return defaultCatch(throwable);
        }
    }

    private ResponseEntity<String> defaultCatch(Throwable throwable) {
        createLoanExecutor.execute(() -> errorService.onError(throwable));

        String errorMessage = null;
        Locale locale = Configuration.getDefaultLocale();
        if (throwable instanceof SqlErrorMessageException) {
            if (((SqlErrorMessageException) throwable).getMessageBody() != null) {
                errorMessage = ((SqlErrorMessageException) throwable).getMessageBody();
            } else if (((SqlErrorMessageException) throwable).getMessageKey() != null) {
                errorMessage = messageSource.getMessage(((SqlErrorMessageException) throwable).getMessageKey(), null, locale);
            }
        } else {
            errorMessage = messageSource.getMessage("system.error.default", null, locale);
        }

        logger.warn(errorMessage);

        return AjaxResponse.errorMessage(errorMessage);
    }
}
