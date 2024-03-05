package com.affirm.common.service.entities;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.catalog.InteractionType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.InteractionService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("credigobService")
public class CredigobService {

    private static final Logger logger = Logger.getLogger(CredigobService.class);

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private FileService fileService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private PersonDAO personDao;

    public static final String CONTRATO = "FORMULARIO_CREDIGOB_VF.pdf";

    public void sendApproveLoanApplicationEmail(LoanApplication loanApplication, Credit credit, Person person) throws Exception {

        PersonContactInformation personContact = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), person.getId());

        JSONObject jsonVars = new JSONObject();
        jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
        jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
        jsonVars.put("ENTITY", credit.getEntity().getFullName());
        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        int interactionContentId = InteractionContent.CREDIGOB_APROVED_LOAN_APPLICATION;

        PersonInteraction interaction = new PersonInteraction();
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
        interaction.setDestination(personContact.getEmail());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setCreditId(credit.getId());
        interaction.setPersonId(person.getId());
        interaction.setSenderName("Credigob");

        byte[] bytes = fileService.getAssociatedFile(CONTRATO);
        PersonInteractionAttachment atachment = new PersonInteractionAttachment();
        atachment.setBytes(bytes);
        atachment.setFilename(CONTRATO);
        interaction.setAttachments(Arrays.asList(atachment));

        interactionService.sendPersonInteraction(interaction, jsonVars, null);
    }
}
