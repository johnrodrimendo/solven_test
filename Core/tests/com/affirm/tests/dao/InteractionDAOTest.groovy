package com.affirm.tests.dao

import com.affirm.common.dao.InteractionDAO
import com.affirm.common.model.catalog.InteractionContent
import com.affirm.common.model.catalog.InteractionType
import com.affirm.common.model.transactional.PersonInteraction
import com.affirm.common.model.transactional.PersonInteractionAttachment
import com.affirm.common.model.transactional.PersonInteractionStat
import com.affirm.common.model.transactional.VerificationCallRequest
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

import static org.junit.jupiter.api.Assertions.*

class InteractionDAOTest extends BaseConfig {

    @Autowired
    private InteractionDAO interactionDAO

    private static final String PHONENUMBER = '926105802'

    private static final Integer PERSON_INTERACTION_ID = 10021
    private static final Integer PERSON_ID = 8944
    private static final Integer LOAN_APPLICATION_ID = 9422
    private static final Integer CREDIT_ID = 1058

    PersonInteraction interaction = new PersonInteraction()

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        interaction.setCreditId(CREDIT_ID)
        interaction.setLoanApplicationId(LOAN_APPLICATION_ID)
        interaction.setPersonId(PERSON_ID)

        InteractionType type = new InteractionType()
        type.id = 1
        interaction.setInteractionType(type)

        InteractionContent content = new InteractionContent()
        content.id = 1
        interaction.setInteractionContent(content)
        interaction.setDestination('dalvera@hotmail.es')
        interaction.setSubject('DAO test')
        interaction.setBody('This is a test made with Junit')
        interaction.setDetail('HU284')

        List<PersonInteractionAttachment> attachmentList = new ArrayList<>()
        PersonInteractionAttachment attachment = new PersonInteractionAttachment()
        attachment.userFileId = 1
        attachmentList.add(attachment)
        interaction.setAttachments(attachmentList)

    }

    @Test
    void shouldInsertPersonInteraction() {
        Integer inserted = interactionDAO.insertPersonInteraction(interaction)

        assertNotNull(inserted)
    }

    @Test
    void shouldGetPersonInteractions() {
        List<PersonInteraction> personInteractionList = interactionDAO.getPersonInteractions(PERSON_ID, Configuration.defaultLocale)

        assertNotNull(personInteractionList)
    }

    @Test
    void shouldGetPersonInteractionsByLoanApplication() {
        List<PersonInteraction> personInteractionList = interactionDAO.getPersonInteractionsByLoanApplication(PERSON_ID, LOAN_APPLICATION_ID, Configuration.defaultLocale)

        assertNotNull(personInteractionList)
    }

    @Test
    void shouldGetPersonInteractionById() {
        PersonInteraction personInteraction = interactionDAO.getPersonInteractionById(PERSON_INTERACTION_ID, Configuration.defaultLocale)

        assertNotNull(personInteraction)
    }

    @Test
    void shouldUpdateSentPersonInteraction() {
        Executable executable = { interactionDAO.updateSentPersonInteraction(PERSON_INTERACTION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldInsertPersonInteractionStatus() {
        Executable executable = {
            interactionDAO.insertPersonInteractionStatus(PERSON_INTERACTION_ID, 'unit test', new Timestamp(System.currentTimeMillis()))
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetPersonInteractionStat() {
        List<PersonInteractionStat> personInteractionStatList = interactionDAO.getPersonInteractionStat(PERSON_INTERACTION_ID)

        assertNotNull(personInteractionStatList)
    }

    @Test
    void shouldUpdateInteraction() {
        interaction.id = 10022
        Executable executable = { interactionDAO.updateInteraction(interaction) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldInsertCallRequestInteraction() {
        Integer peruInserted = interactionDAO.insertCallRequestInteraction(LOAN_APPLICATION_ID, 'PE', PHONENUMBER)
        Integer argentinaInserted = interactionDAO.insertCallRequestInteraction(LOAN_APPLICATION_ID, 'AR', PHONENUMBER)

        assertNotNull(peruInserted)
        assertNotNull(argentinaInserted)
    }

    @Test
    void shouldGetVerificationCallRequestInteraction() {
        int peruLoanApplicationId = 8933
        List<VerificationCallRequest> peruVerificationCallRequestList = interactionDAO.getVerificationCallRequestInteraction(peruLoanApplicationId, 'PE', PHONENUMBER)
        int argentinaLoanApplicationId = 0
        List<VerificationCallRequest> argentinaVerificationCallRequestList = interactionDAO.getVerificationCallRequestInteraction(argentinaLoanApplicationId, 'AR', PHONENUMBER)

        assertNull(peruVerificationCallRequestList)
        assertNull(argentinaVerificationCallRequestList)
    }

}
