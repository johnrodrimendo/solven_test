package com.affirm.client.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InteractionController {

    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @RequestMapping(value = "/fup/unsubscribe/{encryptedEmail}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public ResponseEntity<String> doubleOptInUnsubscribe(
            @PathVariable("encryptedEmail") String encryptedEmail
    ) {
        return ResponseEntity.ok("<h3>Estás a punto de desuscribirte a este correo ¿Estás seguro?</h3>" +
                "<a tabindex=\"1\" title=\"Desuscribirme\" href=\"/fup/unsubscribe/step-2?token=" + encryptedEmail + "\">Sí, quiero dejar de recibir estos correos</a>");
    }

    @RequestMapping(value = "/fup/unsubscribe/step-2", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public ResponseEntity<String> unsubscribeFromTransactionalEmails(
            @RequestParam("token") String encryptedEmail
    ) throws Exception {
        interactionService.registerUnsubscription(encryptedEmail);
        return ResponseEntity.ok("<h1>Te haz desuscrito a este email</h1><p>Grácias por tu tiempo</p>");
    }
}
