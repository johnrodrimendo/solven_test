package com.affirm.entityExt.controller;

import com.affirm.client.model.CreditExtranetPainter;
import com.affirm.client.model.LoanApplicationExtranetPainter;
import com.affirm.client.model.form.AsociateSocialNetworkForm;
import com.affirm.client.model.form.RegisterCellphoneForm;
import com.affirm.client.service.ExtranetService;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.NetworkProfile;
import com.affirm.security.model.ClientToken;
import com.affirm.security.model.PhantomToken;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.auth.oauth2.TokenResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class ExtranetController {

    private static Logger logger = Logger.getLogger(ExtranetController.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private ExtranetService extranetService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private OauthService oauthService;
    @Autowired
    private CatalogService catalogService;

    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public String extranet(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "redirect:/client/dashboard";
    }

    @RequestMapping(value = "/client/login", method = RequestMethod.GET)
    public String extranetLogin(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "/clientExtranet/extranetLogin";
    }

    @RequestMapping(value = "/client/login", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object doExtranetLogin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "fbAccessToken", required = false) String fbAccessToken,
            @RequestParam(value = "liAccessToken", required = false) String liAccessToken,
            @RequestParam(value = "goAccessToken", required = false) String goAccessToken,
            @RequestParam(value = "wiAccessToken", required = false) String wiAccessToken,
            @RequestParam(value = "yaAccessToken", required = false) String yaAccessToken,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password) throws Exception {

        // It's always a mess when you overthink it
        ClientToken token = new ClientToken(locale, request);
        if (fbAccessToken != null && !fbAccessToken.isEmpty()) {
            token.setFacebookCode(fbAccessToken);
        } else if (liAccessToken != null && !liAccessToken.isEmpty()) {
            token.setLinkedinCode(liAccessToken);
        } else if (goAccessToken != null && !goAccessToken.isEmpty()) {
            token.setGoogleCode(goAccessToken);
        } else if (wiAccessToken != null && !wiAccessToken.isEmpty()) {
            token.setWindowsCode(wiAccessToken);
        } else if (yaAccessToken != null && !yaAccessToken.isEmpty()) {
            token.setYahooCode(yaAccessToken);
        } else if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            token.setEmail(email.toLowerCase().trim());
            token.setPassword(password);
        } else {
            return AjaxResponse.errorMessage("Login inválido");
        }

        // Try Catch just to catch the NoUserFoundException for appropiate error message
        try {
            extranetService.login(token, request, null);
        } catch (AuthenticationException ex) {
            return AjaxResponse.errorMessage("No existe usuario asociado a esta cuenta");
        }

        return AjaxResponse.redirect(request.getContextPath() + "/client/dashboard");
    }

    @RequestMapping(value = "/extranetphantomlogin/{encrypt}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String extranetPhantomLogin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("encrypt") String encrypt) throws Exception {

        JSONObject jsonDecrypted = new JSONObject(CryptoUtil.decrypt(encrypt));

        User userToLogin = userDao.getUser(jsonDecrypted.getInt("user"));

        // Validate if the phantomLoginGate is open
        if (userToLogin.getNoAuthLinkExpiration() != null) {

            // Close the gate
            userDao.registerNoAuthExtranetLinkExpiration(userToLogin.getId(), null);

            // Validate if the phantomLoginGate is active
            if (new Date().before(userToLogin.getNoAuthLinkExpiration()) &&
                    (jsonDecrypted.has("user") && jsonDecrypted.has("sysUser"))) {

                // Logout if there is an active session
                if (SecurityUtils.getSubject().isAuthenticated() && SecurityUtils.getSubject().getSession().getAttribute("extranetLoginId") != null) {
                    extranetService.onLogout((int) SecurityUtils.getSubject().getSession().getAttribute("extranetLoginId"), new Date());
                }

                // Login and redrect
                extranetService.login(new PhantomToken(userToLogin.getId(), jsonDecrypted.getInt("sysUser"), locale), request, jsonDecrypted.getInt("sysUser"));
                return "redirect:/client/dashboard";
            }
        }

        return "/clientExtranet/extranetLogin";
    }

    @RequestMapping(value = "/client/logout", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object doExtranetLogout(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        extranetService.onLogout(Integer.parseInt(SecurityUtils.getSubject().getSession().getAttribute("extranetLoginId").toString()), new Date());
        SecurityUtils.getSubject().logout();
        return AjaxResponse.redirect(request.getContextPath());
    }

    @RequestMapping(value = "/client/dashboard", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetDashboard(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {


        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // Return data from loan application
        List<LoanApplication> activeLoanApps = loanApplicationDao.getActiveLoanApplicationsByPerson(locale, user.getPersonId(), LoanApplication.class);
        if (activeLoanApps != null) {
            model.addAttribute("activeLoanApplicationSize", activeLoanApps.size());
        }

        Integer[] activeCreditsIds = creditDao.getActiveCreditIdsByPerson(locale, user.getPersonId(), Entity.AFFIRM);

        if (activeCreditsIds != null) {
            model.addAttribute("activeCreditSize", activeCreditsIds.length);

            double totalPendingAmount = 0;
            for (int i = 0; i < activeCreditsIds.length; i++) {
                Credit credit = creditDao.getCreditByID(activeCreditsIds[i], locale, true, Credit.class);
                if (credit.getTotalPendingAmmount() != null)
                    totalPendingAmount = totalPendingAmount + credit.getTotalPendingAmmount();
            }
            model.addAttribute("totalPendingAmount", totalPendingAmount);
        }


        // TODO The avatar should be saved in the User object
        // User avatar
        UserFacebook facebook;
        PersonLinkedIn linkedin;
        if ((facebook = userDao.getUserFacebook(user.getId())) != null) {
            model.addAttribute("avatarUrl", facebook.getFacebookPicture());
        } else if ((linkedin = userDao.getLinkedin(user.getPersonId())) != null) {
            model.addAttribute("avatarUrl", linkedin.getLinkedinPictureUrl());
        }

        return "/clientExtranet/extranetDashboard";
    }

    @RequestMapping(value = "/client/mis-solicitudes", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetRequests(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // Return data from loan application
        List<LoanApplicationExtranetPainter> activeLoanApps = loanApplicationDao.getActiveLoanApplicationsByPerson(locale, user.getPersonId(), LoanApplicationExtranetPainter.class);
        if (activeLoanApps != null) {
            for (LoanApplicationExtranetPainter l : activeLoanApps) {
                if (l.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL) {
                    l.setWaitingForApproval(true);
                    l.setPrincipalOcupation(personDao//TODO PERSON DEPENDS ON LOAN APPLICATION? ESTEBAN
                            .getPersonOcupationalInformation(locale, user.getPersonId())
                            .stream()
                            .filter(o -> o.getNumber() == 1)
                            .findFirst()
                            .orElse(null));
                    l.setLoanApplicationUserFiles(
                            personDao
                                    .getUserFiles(user.getPersonId(), locale)
                                    .stream()
                                    .filter(x -> x.getLoanApplicationId().intValue() == l.getId())
                                    .findFirst()
                                    .orElse(null));
                } else if (l.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC || l.getStatus().getId() == LoanApplicationStatus.REJECTED) {
                    LoanApplicationPreliminaryEvaluation preEvaluation = loanApplicationService.getLastPreliminaryEvaluation(l.getId(), locale, null);
                    if (preEvaluation != null && preEvaluation.getApproved() != null && !preEvaluation.getApproved()) {
                        l.setRejectedMessage(preEvaluation.getHardFilterMessage());
                        l.setRejectedExpirationDate(preEvaluation.getEvaluationExpirationDate());
                    } else {
                        LoanApplicationEvaluation evaluation = loanApplicationService.getLastEvaluation(l.getId(), l.getPersonId(), locale);
                        if (evaluation != null && evaluation.getApproved() != null && !evaluation.getApproved()) {
                            l.setRejectedMessage(evaluation.getPolicyMessage());
                            l.setRejectedExpirationDate(evaluation.getEvaluationExpirationDate());
                        }
                    }
                }
            }
            model.addAttribute("loanApplications", activeLoanApps);
        }

        // TODO The avatar should be saved in the User object
        // User avatar
        UserFacebook facebook;
        PersonLinkedIn linkedin;
        if ((facebook = userDao.getUserFacebook(user.getId())) != null) {
            model.addAttribute("avatarUrl", facebook.getFacebookPicture());
        } else if ((linkedin = userDao.getLinkedin(user.getPersonId())) != null) {
            model.addAttribute("avatarUrl", linkedin.getLinkedinPictureUrl());
        }

        return "/clientExtranet/extranetRequests";
    }

    @RequestMapping(value = "/client/mis-creditos", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetLoans(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // Return data from loan credit
        Integer[] activeCreditsIds = creditDao.getActiveCreditIdsByPerson(locale, user.getPersonId(), Entity.AFFIRM);
        if (activeCreditsIds != null) {

            List<CreditExtranetPainter> credits = new ArrayList<>();
            for (int i = 0; i < activeCreditsIds.length; i++) {

                CreditExtranetPainter credit = creditDao.getCreditByID(activeCreditsIds[i], locale, true, CreditExtranetPainter.class);
                if (credit.getStatus().getId() == CreditStatus.ACTIVE) {
                    // Get the next pending payment
                    if (credit.getManagementSchedule() != null) {
                        for (ManagementSchedule ms : credit.getManagementSchedule()) {
                            if (ms.getInstallmentStatusId() == ManagementSchedule.STATUS_PENDING) {
                                credit.setNextPayment(ms);
                                break;
                            }
                        }
                    }
                } else if (credit.getStatus().getId() == CreditStatus.INACTIVE_WO_SCHEDULE || credit.getStatus().getId() == CreditStatus.INACTIVE_W_SCHEDULE) {
                    PersonBankAccountInformation bankAccount = personDao.getPersonBankAccountInformation(locale, user.getPersonId());
                    if (bankAccount != null && bankAccount.getBankAccount() != null) {
                        credit.setDisbursementAccount("***************" + bankAccount.getBankAccount().substring(bankAccount.getBankAccount().length() - 4, bankAccount.getBankAccount().length()));
                    }
                }

                // Get the link to the contract pdf
                List<UserFile> userFiles = new ArrayList<UserFile>();
                if (credit.getContractUserFileId() != null && credit.getContractUserFileId().size() > 0) {
                    for (int j = 0; j < credit.getContractUserFileId().size(); j++) {
                        UserFile userFile = userDao.getUserFile(credit.getContractUserFileId().get(j));
                        userFiles.add(userFile);
                    }
                    credit.setContractFile(userFiles);

                }

                credits.add(credit);
            }

            model.addAttribute("credits", credits);


            // Get the last interaction of the credit
            /*List<PersonInteraction> personInteractions = interactionDao.getPersonInteractions(user.getPersonId(), locale);
            if (personInteractions != null && !personInteractions.isEmpty()) {
                for (PersonInteraction interaction : personInteractions) {
                    if (interaction.getInteractionType().getId() == InteractionType.MAIL &&
                            (interaction.getLoanApplicationId() != null && interaction.getLoanApplicationId().equals(credit.getLoanApplicationId())) ||
                            (interaction.getCreditId() != null && interaction.getCreditId().equals(credit.getId()))) {
                        model.addAttribute("lastInteraction", interaction);
                        break;
                    }
                }
            }*/


        }

        // TODO The avatar should be saved in the User object
        // User avatar
        UserFacebook facebook;
        PersonLinkedIn linkedin;
        if ((facebook = userDao.getUserFacebook(user.getId())) != null) {
            model.addAttribute("avatarUrl", facebook.getFacebookPicture());
        } else if ((linkedin = userDao.getLinkedin(user.getPersonId())) != null) {
            model.addAttribute("avatarUrl", linkedin.getLinkedinPictureUrl());
        }

        return "/clientExtranet/extranetLoans";
    }

    @RequestMapping(value = "/client/perfil", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetUserProfile(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {


        User user = (User) SecurityUtils.getSubject().getPrincipal();

        model.addAttribute("person", personDao.getPerson(catalogService, locale, user.getPersonId(), false));

        // TODO The avatar should be saved in the User object
        // User avatar
        UserFacebook facebook;
        PersonLinkedIn linkedin;
        UserNetworkToken google;
        UserNetworkToken yahoo;
        UserNetworkToken windows;
        UserNetworkToken mercadolibre;

        if ((facebook = userDao.getUserFacebook(user.getId())) != null) {
            model.addAttribute("facebook", facebook);
        }
        if ((linkedin = userDao.getLinkedin(user.getPersonId())) != null) {
            model.addAttribute("linkedin", linkedin);
        }
        if ((google = userDao.getUserNetworkTokenByProvider(user.getId(), 'G')) != null) {
            model.addAttribute("google", google);
        }
        if ((yahoo = userDao.getUserNetworkTokenByProvider(user.getId(), 'Y')) != null) {
            model.addAttribute("yahoo", yahoo);
        }
        if ((windows = userDao.getUserNetworkTokenByProvider(user.getId(), 'W')) != null) {
            model.addAttribute("windows", windows);
        }
        if ((mercadolibre = userDao.getUserNetworkTokenByProvider(user.getId(), 'M')) != null) {
            model.addAttribute("mercadolibre", mercadolibre);
        }

        if (facebook != null) {
            model.addAttribute("pictureUrl", facebook.getFacebookPicture());
        } else if (linkedin != null) {
            model.addAttribute("pictureUrl", linkedin.getLinkedinPictureUrl());
        }

        return "/clientExtranet/extranetUserProfile";
    }


    @RequestMapping(value = "/client/notificaciones", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetNotifications(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        List<PersonInteraction> personInteractions = interactionDao.getPersonInteractions(user.getPersonId(), locale);
        model.addAttribute("interactions", personInteractions);
        return "/clientExtranet/extranetNotifications";
    }


    @RequestMapping(value = "/client/dashboard/documentation/panel", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getExtranetDashboardDocumentationPanel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // Return data from loan application
        List<LoanApplication> activeLoanApp = loanApplicationDao.getActiveLoanApplicationsByPerson(locale, user.getPersonId(), LoanApplication.class);
        if (activeLoanApp != null) {
            if (activeLoanApp.stream().filter(l -> l.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL).findAny().orElse(null) != null) {
                model.addAttribute("principalOcupation", personDao.getPersonOcupationalInformation(locale, user.getPersonId()).stream().filter(o -> o.getNumber() == 1).findFirst().orElse(null));
                model.addAttribute("files", personDao.getUserFiles(user.getPersonId(), locale).get(0).getUserFileList());
                return "/fragments/extranetFragment :: #documentationPanel";
            }
        }
        throw new Exception("There is no active loan application");
    }

    @RequestMapping(value = "/client/userFile/{jsonEncrypt}/image.jpg", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public ResponseEntity getUserFile(
            ModelMap model, Locale locale,
            @PathVariable("jsonEncrypt") String jsonEncrypt,
            @RequestParam(required = false) boolean thumbnail) throws Exception {
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
        byte[] file = fileService.getUserFile(user.getId(), jsonDecrypt.getString("file"), thumbnail);
        if (file != null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            String extension = FilenameUtils.getExtension(jsonDecrypt.getString("file"));
            switch (extension.toLowerCase()) {
                case "pdf":
                    responseHeaders.setContentType(MediaType.APPLICATION_PDF);
                    break;
                case "png":
                    responseHeaders.setContentType(MediaType.IMAGE_PNG);
                    break;
                case "gif":
                    responseHeaders.setContentType(MediaType.IMAGE_GIF);
                    break;
                default:
                    responseHeaders.setContentType(MediaType.IMAGE_JPEG);
                    break;
            }
            return new ResponseEntity(file, responseHeaders, HttpStatus.OK);
        }
        return null;
    }

    @RequestMapping(value = "/client/file/userFile", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> registerUserFile(
            ModelMap model, Locale locale,
            @RequestParam("file") MultipartFile[] file,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("userFileType") Integer userFileType) throws Exception {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        userService.registerUserFiles(file, loanApplicationId, user.getId(), userFileType);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/client/refreshSession", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> refreshSession(Locale locale) throws Exception {
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/client/addsocialnetwork", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> refreshSession(
            ModelMap model, Locale locale, AsociateSocialNetworkForm form) throws Exception {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // Validate the form
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorMessage("Los valores no son válidos");
        }

        // Get the accessToken
        TokenResponse tokenResponse = oauthService.getAccessToken(form.getOauthNetworkByProvider(), form.getAuthCode());

        // Get the email
        NetworkProfile networkProfile = oauthService.getNetworkProfile(form.getOauthNetworkByProvider(), tokenResponse.getAccessToken());

        // Save the access token and email
        UserNetworkToken networkToken = userDao.registerNetworkAccessToken(user.getId(),
                form.getSocialNetwork(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                networkProfile.getEmail(), networkProfile.getId());
        userDao.updateNetworkProfile(networkToken.getId(), networkProfile.getResponse().toString());

        switch (form.getOauthNetworkByProvider()) {
            case FACEBOOK:
                UserFacebook userFacebbok = new UserFacebook();
                userFacebbok.fillFromApi(networkProfile.getResponse());
                userFacebbok.setFacebookFriends(oauthService.requestFriendsCount(Configuration.OauthNetwork.FACEBOOK, tokenResponse.getAccessToken()));
                userDao.registerFacebook(user.getId(), userFacebbok);
                break;
            case LINKEDIN:
                PersonLinkedIn personLinkedin = new PersonLinkedIn();
                personLinkedin.fillFromApi(networkProfile.getResponse());
                userDao.registerLinkedin(user.getPersonId(), personLinkedin);
                break;
            case GOOGLE:
            case WINDOWS:
            case YAHOO:
                // Execute the get data of the user email in the worker
                webscrapperService.callUserEmailDataBot(user.getId(), form.getSocialNetwork());
                break;
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/client/update/cellphone", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateCellphone(
            ModelMap model, Locale locale, RegisterCellphoneForm form) throws Exception {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        userDao.registerPhoneNumber(user.getId(), form.getCountryCode(), form.getCellphone());
        user.setPhoneNumber(form.getCellphone());

        return AjaxResponse.ok(null);
    }
}
