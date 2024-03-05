package com.affirm.backoffice.service.impl;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.backoffice.model.LoanApplicationBoPainter;
import com.affirm.backoffice.model.PersonInteractionPainter;
import com.affirm.backoffice.service.ApplicationPainterService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.InteractionDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.model.transactional.LoanApplicationUserFiles;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.FileService;
import com.affirm.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ApplicationPainterServiceImp implements ApplicationPainterService {

    @Autowired
    LoanApplicationDAO loanApplicationDao;
    @Autowired
    PersonDAO personDao;
    @Autowired
    private UserService userService;
    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private FileService fileService;

    @Override
    public List<LoanApplicationBoPainter> getApplicationsByPerson(Integer personId, Locale locale) throws Exception {
        List<LoanApplicationBoPainter> applications = loanApplicationDao.getLoanApplicationsByPerson(locale, personId, LoanApplicationBoPainter.class);
//        List<LoanApplicationUserFiles> userFilesObjectList = personDao.getUserFiles(personId, locale);
//        List<RecognitionResultsPainter> recognitions = loanApplicationDao.getRecognitionResults(personId, locale);

        if (applications != null) {
            for (LoanApplicationBoPainter loanApplication : applications) {
                loanApplication.setOffers(loanApplicationDao.getLoanOffersAll(loanApplication.getId()));
                loanApplication.setConsolidableDebts(loanApplicationDao.getConsolidationAccounts(loanApplication.getId()));

                if (loanApplication.getEntityUserId() != null)
                    loanApplication.setEntityUser(userService.getUserEntityById(loanApplication.getEntityUserId(), locale));

//                ACORDEON DOCUMENTATION
//                for (LoanApplicationUserFiles loanApplicationUserFiles : userFilesObjectList) {
//                    if (loanApplicationUserFiles.getLoanApplicationCode() == null) continue;
//                    if (loanApplicationUserFiles.getLoanApplicationCode().equals(loanApplication.getCode())) {
//                        loanApplication.setUserFilesObjectList(loanApplicationUserFiles);
//                    }
//                }

//                ACORDEON REKOGNITION
//                for (int j = 0; recognitions != null && j < recognitions.size(); ++j) {
//                    if (recognitions.get(j).getLoanApplicationId() == null) continue;
//                    if (recognitions.get(j).getLoanApplicationId().equals(loanApplication.getId())) {
//                        loanApplication.setRecognition(recognitions.get(j));
//                    }
//                }

                loanApplication.setAuditRejectionReasons(creditDao.getAuditRejectionReason(loanApplication.getId()));

//                ACORDEON INTERACTIONS
//                List<PersonInteraction> personInteractions = interactionDao.getPersonInteractionsByLoanApplication(personId, loanApplication.getId(), locale);
//                List<Object> interactions = personInteractions != null ? personInteractions.stream().filter(pi -> loanApplication.getId().equals(pi.getLoanApplicationId())).collect(Collectors.toList()) : new ArrayList<>();
//                PersonInteractionPainter painter = new PersonInteractionPainter();
//                painter.setLoanApplicationId(loanApplication.getId());
//                painter.setLoanApplicationCode(loanApplication.getCode());
//                painter.setRegisterDate(loanApplication.getRegisterDate());
//                painter.setInteractions(interactions);
//
//                loanApplication.setInteractions(painter);

                loanApplication.setShowFiles(true);
                loanApplication.setShowInteractions(true);
                loanApplication.setShowRecognition(true);
            }
        }

        return applications;
    }

    @Override
    public LoanApplicationBoPainter getApplicationById(int loanApplicationId, Locale locale, HttpServletRequest request) throws Exception {
        LoanApplicationBoPainter application = loanApplicationDao.getLoanApplication(loanApplicationId, locale, LoanApplicationBoPainter.class);

        application.setOffers(loanApplicationDao.getLoanOffersAll(application.getId()));
        application.setConsolidableDebts(loanApplicationDao.getConsolidationAccounts(application.getId()));
        if (application.getEntityUserId() != null)
            application.setEntityUser(userService.getUserEntityById(application.getEntityUserId(), locale));

        application.setShowFiles(false);
        application.setShowInteractions(false);
        application.setShowRecognition(false);

        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(application.getId());
        if (userFiles != null && userFiles.size() > 0) {
            UserFile userFile = userFiles.stream().filter(e -> e.getFileType().getId() == UserFileType.CONTRACT_CALL).findFirst().orElse(null);
            if (userFile != null) {
                String tokyCallURL = fileService.generateUserFileUrl(userFile.getId(), request, false);
                application.setTokyCall(tokyCallURL);
            }
        }

        return application;
    }

}
