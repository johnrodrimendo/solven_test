package com.affirm.common.service;

import com.affirm.common.dao.*;
import com.affirm.common.model.BankAccountOfferData;
import com.affirm.common.model.FunnelStep;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository("pixelConversionService")
public class PixelConversionService {

    public static final String PRE_APPROVED_CONVERSION = "preApproved";
    public static final String PERSONAL_DATA_COMPLETED_CONVERSION = "perDatCompleted";
    public static final String OFFER_SHOWED_CONVERSION = "offerShowed";
    public static final String OFFER_SELECTED_CONVERSION = "offerSelected";
    public static final String VERIFICATION_COMPLETED_CONVERSION = "verifCompleted";
    public static final String FACEBOOK_SOME_REJECTION = "some_rejection_facebook";
    public static final String SOME_REJECTION = "some_rejection";
    public static final String GOOGLE_SOURCE = "google";
    public static final String FACEBOOK_SOURCE = "facebook";
    public static final String BANBIF_STEP1_CONVERSION = "banbifStep1"; // Registro
    public static final String BANBIF_STEP2_CONVERSION = "banbifStep2"; // En pin o buen score
    public static final String BANBIF_STEP3_CONVERSION = "banbifStep3"; // En preguntas reto
    public static final String BANBIF_STEP4_CONVERSION = "banbifStep4"; // En oferta
    public static final String BANBIF_STEP5_CONVERSION = "banbifStep5"; // En Domicilio vivienda
    public static final String BANBIF_STEP6_CONVERSION = "banbifStep6"; // En domicilio laboral
    public static final String BANBIF_STEP7_CONVERSION = "banbifStep7"; // Final
    public static final String BANBIF_STEP8_CONVERSION = "banbifStep8"; // Desemnbolso
    public static final String AZTECA_STEP2_CONVERSION = "aztecaStep2";
    public static final String AZTECA_STEP3_CONVERSION = "aztecaStep3";
    public static final String AZTECA_STEP4_CONVERSION = "aztecaStep4";
    public static final String AZTECA_STEP5_CONVERSION = "aztecaStep5";
    public static final String AZTECA_STEP6_CONVERSION = "aztecaStep6";
    public static final String AZTECA_STEP1_CONVERSION = "aztecaStep1";
    public static final String AZTECA_CONSUMO_FB_STEP1_CONVERSION = "aztecaFbStep1";
    public static final String AZTECA_CONSUMO_FB_STEP2_CONVERSION = "aztecaFbStep2";
    public static final String AZTECA_CONSUMO_FB_STEP3_CONVERSION = "aztecaFbStep3";
    public static final String AZTECA_CONSUMO_FB_STEP4_CONVERSION = "aztecaFbStep4";
    public static final String AZTECA_CONSUMO_FB_STEP5_CONVERSION = "aztecaFbStep5";
    public static final String AZTECA_CONSUMO_FB_STEP6_CONVERSION = "aztecaFbStep6";
    public static final String AZTECA_CONSUMO_FL_STEP1_CONVERSION = "aztecaFlStep1"; // This one is called from the frontend
    public static final String AZTECA_CONSUMO_FL_STEP2_CONVERSION = "aztecaFlStep2";
    public static final String AZTECA_CONSUMO_FL_STEP3_CONVERSION = "aztecaFlStep3";
    public static final String AZTECA_CONSUMO_FL_STEP4_CONVERSION = "aztecaFlStep4";
    public static final String AZTECA_CONSUMO_FL_STEP5_CONVERSION = "aztecaFlStep5";
    public static final String AZTECA_CONSUMO_FL_STEP6_CONVERSION = "aztecaFlStep6";
    public static final String AZTECA_CONSUMO_FL_STEP7_CONVERSION = "aztecaFlStep7";
    public static final String AZTECA_CONSUMO_FL_STEP8_CONVERSION = "aztecaFlStep8";
    public static final String AZTECA_CONSUMO_FL_STEP9_CONVERSION = "aztecaFlStep9";
    public static final String AZTECA_CONSUMO_FL_STEP10_CONVERSION = "aztecaFlStep10";
    public static final String AZTECA_CONSUMO_FL_STEP11_CONVERSION = "aztecaFlStep11";
    public static final String AZTECA_CONSUMO_FL_STEP12_CONVERSION = "aztecaFlStep12";
    public static final String AZTECA_CONSUMO_FL_STEP13_CONVERSION = "aztecaFlStep13";
    public static final String AZTECA_CONSUMO_FL_STEP14_CONVERSION = "aztecaFlStep14";
    public static final String AZTECA_CONSUMO_FL_STEP15_CONVERSION = "aztecaFlStep15";
    public static final String AZTECA_CONSUMO_FL_STEP16_CONVERSION = "aztecaFlStep16";
    public static final String AZTECA_CONSUMO_FL_STEP17_CONVERSION = "aztecaFlStep17";
    public static final String AZTECA_CONSUMO_FL_STEP18_CONVERSION = "aztecaFlStep18";
    public static final String AZTECA_CONSUMO_FL_STEP19_CONVERSION = "aztecaFlStep19";
    public static final String AZTECA_CONSUMO_FL_STEP20_CONVERSION = "aztecaFlStep20";
    public static final String AZTECA_CONSUMO_FL_STEP21_CONVERSION = "aztecaFlStep21";
    public static final String AZTECA_CONSUMO_FL_STEP22_CONVERSION = "aztecaFlStep22";
    public static final String AZTECA_CONSUMO_FL_STEP23_CONVERSION = "aztecaFlStep23";
    public static final String AZTECA_CONSUMO_FL_STEP24_CONVERSION = "aztecaFlStep24";
    public static final String AZTECA_CONSUMO_FL_STEP25_CONVERSION = "aztecaFlStep25";
    public static final String AZTECA_CONSUMO_FL_STEP26_CONVERSION = "aztecaFlStep26";
    public static final String AZTECA_CONSUMO_FL_STEP27_CONVERSION = "aztecaFlStep27";
    public static final String AZTECA_CONSUMO_FL_STEP28_CONVERSION = "aztecaFlStep28";
    public static final String AZTECA_CONSUMO_FL_STEP29_CONVERSION = "aztecaFlStep29";
    public static final String AZTECA_CUENTA_AHORRO_CLIENTE_VALIDADO = "aztecaAhorroClienteVal";
    public static final String AZTECA_CUENTA_AHORRO_FL_PAGE_LOAD = "aztecaAhorroFlPageLoad";
    public static final String AZTECA_CUENTA_AHORRO_FL_LOAN_REGISTER = "aztecaAhorroFlRegister";
    public static final String AZTECA_CUENTA_AHORRO_FL_LOAD_PIN = "aztecaAhorroFlLoadPin";
    public static final String AZTECA_CUENTA_AHORRO_FL_VALIDATE_PIN = "aztecaAhorroFlValidatePin";
    public static final String AZTECA_CUENTA_AHORRO_FL_DIA_A_DIA_PIN = "aztecaAhorroFlDiaADia";
    public static final String AZTECA_CUENTA_AHORRO_FL_AHORRO_META = "aztecaAhorroFlAhorroMeta";
    public static final String AZTECA_CUENTA_AHORRO_OFFER_SELECTED = "aztecaAhorroOfferSelected";


    @Autowired
    private ConversionDAO conversionDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;
    @Autowired
    private EvaluationDAO evaluationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;

    public JSONArray getConversionToSend(LoanApplication loanApplication) throws Exception {
        JSONArray conversionsToSend = new JSONArray();

        List<Conversion> conversions = conversionDao.getConversions(loanApplication.getId());
        if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) {
            // Step 1 - Registro
            boolean isSetp1Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP1_CONVERSION));
            if (!isSetp1Converted) {
                conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP1_CONVERSION);
                conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP1_CONVERSION);
                JSONObject json = new JSONObject();
                json.put("type", BANBIF_STEP1_CONVERSION);
                conversionsToSend.put(json);
            }
            // Step 2 - Pin o buen score
            boolean isSetp2Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP2_CONVERSION));
            if (!isSetp2Converted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER_PIN || loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.BANBIF_GOOD_SCORE) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP2_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP2_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", BANBIF_STEP2_CONVERSION);
                    conversionsToSend.put(json);
                }
            }
            // Step 3 - Preguntas reto
            boolean isSetp3Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP3_CONVERSION));
            if (!isSetp3Converted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.BANBIF_CHALLENGE_QUESTION) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP3_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP3_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", BANBIF_STEP3_CONVERSION);
                    conversionsToSend.put(json);
                }
            }
            // Step 4 - Oferta
            boolean isSetp4Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP4_CONVERSION));
            if (!isSetp4Converted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.BANBIF_OFFERS) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP4_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP4_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", BANBIF_STEP4_CONVERSION);
                    conversionsToSend.put(json);
                }
            }
            // Step 5 - Domicilio vivienda
            boolean isSetp5Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP5_CONVERSION));
            if (!isSetp5Converted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP5_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP5_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", BANBIF_STEP5_CONVERSION);
                    conversionsToSend.put(json);
                }
            }
            // Step 6 - Domicilio laboral
            boolean isSetp6Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP6_CONVERSION));
            if (!isSetp6Converted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.WORKPLACE_ADDRESS_DISGREGATED) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP6_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP6_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", BANBIF_STEP6_CONVERSION);
                    conversionsToSend.put(json);
                }
            }
            // Step 7 - Final
            boolean isSetp7Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(BANBIF_STEP7_CONVERSION));
            if (!isSetp7Converted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.BANBIF_WAITING_APPROVAL) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, BANBIF_STEP7_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, BANBIF_STEP7_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", BANBIF_STEP7_CONVERSION);
                    conversionsToSend.put(json);
                }
            }
        } else if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {
//            // Step 1 - Registro
//            boolean isSetp1Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_STEP1_CONVERSION));
//            if (!isSetp1Converted) {
//                conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_STEP1_CONVERSION);
//                conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_STEP1_CONVERSION);
//                JSONObject json = new JSONObject();
//                json.put("type", "step1");
//                conversionsToSend.put(json);
//            }
//            // Step 2 - Pin validado
//            boolean isSetp2Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_STEP2_CONVERSION));
//            if (!isSetp2Converted) {
//                if(loanApplication.getFunnelSteps().stream().anyMatch(f -> f.getStepId() == FunnelStep.PRE_EVALUATION_APPROVED)){
//                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_STEP2_CONVERSION);
//                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_STEP2_CONVERSION);
//                    JSONObject json = new JSONObject();
//                    json.put("type", "step2");
//                    conversionsToSend.put(json);
//                }
//            }
//            // Step 3 - Pin validado
//            boolean isSetp3Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_STEP3_CONVERSION));
//            if (!isSetp3Converted) {
//                if(loanApplication.getFunnelSteps().stream().anyMatch(f -> f.getStepId() == FunnelStep.PIN_VALIDATED)){
//                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_STEP3_CONVERSION);
//                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_STEP3_CONVERSION);
//                    JSONObject json = new JSONObject();
//                    json.put("type", "step3");
//                    conversionsToSend.put(json);
//                }
//            }
//            // Step 4 - Solicitud completa
//            boolean isSetp4Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_STEP4_CONVERSION));
//            if (!isSetp4Converted) {
//                if(loanApplication.getFunnelSteps().stream().anyMatch(f -> f.getStepId() == FunnelStep.REQUEST_COMPLETE)){
//                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_STEP4_CONVERSION);
//                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_STEP4_CONVERSION);
//                    JSONObject json = new JSONObject();
//                    json.put("type", "step4");
//                    conversionsToSend.put(json);
//                }
//            }
//            // Step 5 - Solicitud Con oferta
//            boolean isSetp5Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_STEP5_CONVERSION));
//            if (!isSetp5Converted) {
//                if(loanApplication.getFunnelSteps().stream().anyMatch(f -> f.getStepId() == FunnelStep.REQUEST_WITH_OFFER)){
//                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_STEP5_CONVERSION);
//                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_STEP5_CONVERSION);
//                    JSONObject json = new JSONObject();
//                    json.put("type", "step5");
//                    conversionsToSend.put(json);
//                }
//            }
//            // Step 6 - Oferta aceptada
//            boolean isSetp6Converted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_STEP6_CONVERSION));
//            if (!isSetp6Converted) {
//                if(loanApplication.getFunnelSteps().stream().anyMatch(f -> f.getStepId() == FunnelStep.ACCEPTED_OFFER)){
//                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_STEP6_CONVERSION);
//                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_STEP6_CONVERSION);
//                    JSONObject json = new JSONObject();
//                    json.put("type", "step6");
//                    conversionsToSend.put(json);
//                }
//            }

            // New events defined by azteca
            if(loanApplication.getProductCategoryId() == ProductCategory.CONSUMO){
                // Step 1 facebook
                boolean sep1Fb = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FB_STEP1_CONVERSION));
                if (!sep1Fb) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.EMAIL_AND_CELLPHONE) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CONSUMO_FB_STEP1_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FB_STEP1_CONVERSION, "step1");
                    }
                }
                // Step 2 facebook
                boolean sep2Fb = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FB_STEP2_CONVERSION));
                if (!sep2Fb) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER_PIN) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CONSUMO_FB_STEP2_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FB_STEP2_CONVERSION, "step2");
                    }
                }
                // Step 3 facebook
                boolean sep3Fb = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FB_STEP3_CONVERSION));
                if (!sep3Fb) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.MARITAL_STATUS) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CONSUMO_FB_STEP3_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FB_STEP3_CONVERSION, "step3");
                    }
                }
                // Step 4 facebook
                boolean sep4Fb = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FB_STEP4_CONVERSION));
                if (!sep4Fb) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.PARTNER_IDENTIFICATION) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CONSUMO_FB_STEP4_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FB_STEP4_CONVERSION, "step4");
                    }
                }
                // Step 5 facebook
                boolean sep5Fb = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FB_STEP5_CONVERSION));
                if (!sep5Fb) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.PARTNER_IDENTIFICATION) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CONSUMO_FB_STEP5_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FB_STEP5_CONVERSION, "step5");
                    }
                }
                // Step 6 facebook
                boolean sep6Fb = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FB_STEP6_CONVERSION));
                if (!sep6Fb) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CONSUMO_FB_STEP6_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FB_STEP6_CONVERSION, "step6");
                    }
                }
                // Step 2 floodlight
                boolean sep2Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP2_CONVERSION));
                if (!sep2Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.EMAIL_AND_CELLPHONE) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP2_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP2_CONVERSION, "prest_02");
                    }
                }
                // Step 3 floodlight
                boolean sep3Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP3_CONVERSION));
                if (!sep3Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER_PIN) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP3_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP3_CONVERSION, "prest_03");
                    }
                }
                // Step 4 floodlight
                boolean sep4Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP4_CONVERSION));
                if (!sep4Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.MARITAL_STATUS) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP4_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP4_CONVERSION, "prest_04");
                    }
                }
                // Step 5 floodlight
                boolean sep5Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP5_CONVERSION));
                if (!sep5Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.PARTNER_IDENTIFICATION) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP5_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP5_CONVERSION, "prest_05");
                    }
                }
                // Step 6 floodlight
                boolean sep6Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP6_CONVERSION));
                if (!sep6Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.PARTNER_IDENTIFICATION) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP6_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP6_CONVERSION, "prest_06");
                    }
                }
                // Step 7 floodlight
                boolean sep7Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP7_CONVERSION));
                if (!sep7Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP7_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP7_CONVERSION, "prest_07");
                    }
                }
                // Step 8 floodlight
                boolean sep8Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP8_CONVERSION));
                if (!sep8Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.HOME_TYPE) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP8_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP8_CONVERSION, "prest_08");
                    }
                }
                // Step 9 floodlight
                boolean sep9Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP9_CONVERSION));
                if (!sep9Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.STUDIES_LEVEL) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP9_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP9_CONVERSION, "prest_09");
                    }
                }
                // Step 10 floodlight
                boolean sep10Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP10_CONVERSION));
                if (!sep10Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.PRINCIPAL_INCOME_TYPE) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP10_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP10_CONVERSION, "prest_10");
                    }
                }
                // Step 11 floodlight
                boolean sep11Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP11_CONVERSION));
                if (!sep11Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.MERGE_OCUPATION_WORK_PLACE_DEPENDENT_PEP_PROFESSION_AND_OCUPATIONS) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP11_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP11_CONVERSION, "prest_11");
                    }
                }
                // Step 12 floodlight
                boolean sep12Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP12_CONVERSION));
                if (!sep12Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.MERGE_OCUPATION_WORK_PLACE_DEPENDENT_PEP_PROFESSION_AND_OCUPATIONS) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP12_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP12_CONVERSION, "prest_12");
                    }
                }
                // Step 13 floodlight
                boolean sep13Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP13_CONVERSION));
                if (!sep13Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.RUC_WORK_PLACE_DEPENDENT) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP13_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP13_CONVERSION, "prest_13");
                    }
                }
                // Step 14 floodlight
                boolean sep14Fl = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CONSUMO_FL_STEP14_CONVERSION));
                if (!sep14Fl) {
                    if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.OCUPATION_START_DATE_DEPENDENT) {
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CONSUMO_FL_STEP14_CONVERSION);
                        addEventToRegister(conversionsToSend, AZTECA_CONSUMO_FL_STEP14_CONVERSION, "prest_14");
                    }
                }
            }else if(loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA){
                // Azteca Cuenta iCliente Validado
                boolean stepCuentaValClient = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_CLIENTE_VALIDADO));
                if (!stepCuentaValClient) {
                    User user = userDao.getUser(loanApplication.getUserId());
                    if(user.getPhoneVerified() != null && user.getPhoneVerified()){
                        conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, AZTECA_CUENTA_AHORRO_CLIENTE_VALIDADO);
                        addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_CLIENTE_VALIDADO, AZTECA_CUENTA_AHORRO_CLIENTE_VALIDADO);
                    }
                }
                // Floodlight Loan register
                boolean flLoanReg = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_FL_LOAN_REGISTER));
                if (!flLoanReg) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CUENTA_AHORRO_FL_LOAN_REGISTER);
                    addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_FL_LOAN_REGISTER, "ahme_dat");
                }
                // Floodlight Load pin
                boolean flLoadPin = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_FL_LOAD_PIN));
                if (!flLoadPin) {
                    if(loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER_PIN){
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CUENTA_AHORRO_FL_LOAD_PIN);
                        addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_FL_LOAD_PIN, "ahme_pin");
                    }
                }
                // Floodlight Validate pin
                boolean flPinValidated = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_FL_VALIDATE_PIN));
                if (!flPinValidated) {
                    if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getPhoneValidated() != null && loanApplication.getAuxData().getPhoneValidated()){
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CUENTA_AHORRO_FL_VALIDATE_PIN);
                        addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_FL_VALIDATE_PIN, "ahme_bt1");
                    }
                }
                // Floodlight Dia a Dia
                boolean flDiaADia = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_FL_DIA_A_DIA_PIN));
                if (!flDiaADia) {
                    List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                    if(offers != null && offers.stream().anyMatch(o -> o.getSelected() != null && o.getSelected())){
                        LoanOffer selectedOffer = offers.stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
                        if(selectedOffer != null && selectedOffer.getBankAccountOfferData() != null){
                            if(selectedOffer.getBankAccountOfferData().getType() != null && selectedOffer.getBankAccountOfferData().getType() == BankAccountOfferData.TRADITIONAL_TYPE){
                                conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CUENTA_AHORRO_FL_DIA_A_DIA_PIN);
                                addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_FL_DIA_A_DIA_PIN, "ahme_bt2");
                            }
                        }
                    }
                }
                // Floodlight Ahorro Meta
                boolean flAhoMeta = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_FL_AHORRO_META));
                if (!flAhoMeta) {
                    List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                    if(offers != null && offers.stream().anyMatch(o -> o.getSelected() != null && o.getSelected())){
                        LoanOffer selectedOffer = offers.stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
                        if(selectedOffer != null && selectedOffer.getBankAccountOfferData() != null){
                            if(selectedOffer.getBankAccountOfferData().getType() != null && selectedOffer.getBankAccountOfferData().getType() == BankAccountOfferData.HIGH_PROFITABILITY_TYPE){
                                conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CUENTA_AHORRO_FL_AHORRO_META);
                                addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_FL_AHORRO_META, "ahme_bt3");
                            }
                        }
                    }
                }
                // Ahorro oferta seleccionada
                boolean azOfferSelected = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(AZTECA_CUENTA_AHORRO_OFFER_SELECTED));
                if (!azOfferSelected) {
                    List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                    if(offers != null && offers.stream().anyMatch(o -> o.getSelected() != null && o.getSelected())){
                        conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, AZTECA_CUENTA_AHORRO_OFFER_SELECTED);
                        addEventToRegister(conversionsToSend, AZTECA_CUENTA_AHORRO_OFFER_SELECTED, null);
                    }
                }
            }
        } else {
            // Pre Approved
            boolean isPreApprovedConverted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(PRE_APPROVED_CONVERSION));
            if (!isPreApprovedConverted) {
                LoanApplicationPreliminaryEvaluation lape = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null, true);
                if (lape != null && lape.getApproved() != null && lape.getApproved()) {
                    isPreApprovedConverted = true;
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, PRE_APPROVED_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, PRE_APPROVED_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", PRE_APPROVED_CONVERSION);
                    json.put("value", 0.56);

                    boolean approvedCategory = false;
                    if (passAcceso(loanApplication.getPersonId())) {
                        boolean isPreApprovedAcceso = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().anyMatch(p -> p.getEntityId() == Entity.ACCESO && p.getApproved() != null && p.getApproved());
                        if (isPreApprovedAcceso) {
                            approvedCategory = true;
                        }
                    }
                    if (approvedCategory) {
                        json.put("categoryType", PRE_APPROVED_CONVERSION + "A");
                        json.put("categoryValue", 2.4);
                    } else {
                        json.put("categoryType", PRE_APPROVED_CONVERSION + "O");
                        json.put("categoryValue", 0.45);
                    }
                    conversionsToSend.put(json);
                }
            }
            // Personal Data
            boolean isPersonalDataConverted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(PERSONAL_DATA_COMPLETED_CONVERSION));
            if (isPreApprovedConverted && !isPersonalDataConverted) {
                if (loanApplication.getCurrentProcessQuestion().getCategory().getId() == ProcessQuestionCategory.INCOME) {
                    isPersonalDataConverted = true;
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, PERSONAL_DATA_COMPLETED_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, PERSONAL_DATA_COMPLETED_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", PERSONAL_DATA_COMPLETED_CONVERSION);
                    json.put("value", 0.81);

                    boolean approvedCategory = false;
                    if (passAcceso(loanApplication.getPersonId())) {
                        boolean isPreApprovedAcceso = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().anyMatch(p -> p.getEntityId() == Entity.ACCESO && p.getApproved() != null && p.getApproved());
                        if (isPreApprovedAcceso) {
                            approvedCategory = true;
                        }
                    }
                    if (approvedCategory) {
                        json.put("categoryType", PERSONAL_DATA_COMPLETED_CONVERSION + "A");
                        json.put("categoryValue", 3);
                    } else {
                        json.put("categoryType", PERSONAL_DATA_COMPLETED_CONVERSION + "O");
                        json.put("categoryValue", 0.65);
                    }
                    conversionsToSend.put(json);
                }
            }
            // Offer showed
            boolean isOfferShowedConverted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(OFFER_SHOWED_CONVERSION));
            if (isPreApprovedConverted && isPersonalDataConverted && !isOfferShowedConverted) {
                if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.OFFER) {
                    isOfferShowedConverted = true;
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, OFFER_SHOWED_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, OFFER_SHOWED_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", OFFER_SHOWED_CONVERSION);
                    json.put("value", 5);

                    boolean approvedCategory = false;
                    if (passAcceso(loanApplication.getPersonId())) {
                        boolean isApprovedAcceso = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().anyMatch(e -> e.getEntityId() == Entity.ACCESO && e.getApproved() != null && e.getApproved());
                        if (isApprovedAcceso) {
                            approvedCategory = true;
                        }
                    }
                    if (approvedCategory) {
                        json.put("categoryType", OFFER_SHOWED_CONVERSION + "A");
                        json.put("categoryValue", 15);
                    } else {
                        json.put("categoryType", OFFER_SHOWED_CONVERSION + "O");
                        json.put("categoryValue", 4.0);
                    }
                    conversionsToSend.put(json);
                }
            }
            // offer Selected
            boolean isOfferSelectedConverted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(OFFER_SELECTED_CONVERSION));
            if (isPreApprovedConverted && isPersonalDataConverted && isOfferShowedConverted && !isOfferSelectedConverted) {
                if (loanApplication.getCurrentProcessQuestion().getCategory().getId() == ProcessQuestionCategory.VERIFICATION) {
                    isOfferSelectedConverted = true;
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, OFFER_SELECTED_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, OFFER_SELECTED_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", OFFER_SELECTED_CONVERSION);
                    json.put("value", 10);

                    boolean approvedCategory = false;
                    if (passAcceso(loanApplication.getPersonId())) {
                        boolean isApprovedAcceso = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().anyMatch(e -> e.getEntityId() == Entity.ACCESO && e.getApproved() != null && e.getApproved());
                        if (isApprovedAcceso) {
                            approvedCategory = true;
                        }
                    }
                    if (approvedCategory) {
                        json.put("categoryType", OFFER_SELECTED_CONVERSION + "A");
                        json.put("categoryValue", 23);
                    } else {
                        json.put("categoryType", OFFER_SELECTED_CONVERSION + "O");
                        json.put("categoryValue", 8.0);
                    }
                    conversionsToSend.put(json);
                }
            }
            // Verification completed
            boolean isVerificationCompletedConverted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(VERIFICATION_COMPLETED_CONVERSION));
            if (isPreApprovedConverted && isPersonalDataConverted && isOfferShowedConverted && isOfferSelectedConverted && !isVerificationCompletedConverted) {
                if (loanApplication.getCurrentProcessQuestion().getId() == ProcessQuestion.Question.Constants.WAITING_APPROVAL) {
                    isVerificationCompletedConverted = true;
                    conversionDao.registerPixelConversion(loanApplication.getId(), GOOGLE_SOURCE, VERIFICATION_COMPLETED_CONVERSION);
                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, VERIFICATION_COMPLETED_CONVERSION);
                    JSONObject json = new JSONObject();
                    json.put("type", VERIFICATION_COMPLETED_CONVERSION);
                    json.put("value", 13.75);

                    boolean approvedCategory = false;
                    if (passAcceso(loanApplication.getPersonId())) {
                        boolean isApprovedAcceso = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().anyMatch(e -> e.getEntityId() == Entity.ACCESO && e.getApproved() != null && e.getApproved());
                        if (isApprovedAcceso) {
                            approvedCategory = true;
                        }
                    }
                    if (approvedCategory) {
                        json.put("categoryType", VERIFICATION_COMPLETED_CONVERSION + "A");
                        json.put("categoryValue", 28);
                    } else {
                        json.put("categoryType", VERIFICATION_COMPLETED_CONVERSION + "O");
                        json.put("categoryValue", 11);
                    }

                    conversionsToSend.put(json);
                }
            }

            // Rejection conversions
            boolean isSomeRejectionFacebookConverted = conversions.stream().anyMatch(c -> c.getInstance() != null && c.getInstance().equalsIgnoreCase(FACEBOOK_SOME_REJECTION));
            if (!isSomeRejectionFacebookConverted) {
                if (Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC, LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION, LoanApplicationStatus.REJECTED).contains(loanApplication.getStatus().getId())) {
                    Integer rejectionExpirationDays = null;

                    // First try to find the rejection from Evaluations
                    LoanApplicationEvaluation rejectedEvaluation = evaluationDao.getEvaluationsWithPolicies(loanApplication.getId())
                            .stream().filter(e -> e.getApproved() != null && !e.getApproved()).findFirst().orElse(null);
                    if (rejectedEvaluation != null && rejectedEvaluation.getPolicy() != null) {
                        rejectionExpirationDays = rejectedEvaluation.getEvaluationPolicies()
                                .stream()
                                .filter(p -> p.getPolicy().getPolicyId() == rejectedEvaluation.getPolicy().getPolicyId())
                                .findFirst()
                                .map(h -> h.getExpirationDays())
                                .orElse(null);
                    }

                    // If its not a evaluation rejection, find it in pre evaluations
                    if (rejectionExpirationDays == null) {
                        LoanApplicationPreliminaryEvaluation rejectedPreEvaluation = preliminaryEvaluationDao.getPreliminaryEvaluationsWithHardFilters(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().filter(p -> p.getApproved() != null && !p.getApproved()).findFirst().orElse(null);
                        if (rejectedPreEvaluation != null && rejectedPreEvaluation.getHardFilter() != null) {
                            rejectionExpirationDays = rejectedPreEvaluation.getPreliminaryHardFilters()
                                    .stream()
                                    .filter(h -> h.getHardFilter().getId().equals(rejectedPreEvaluation.getHardFilter().getId()))
                                    .findFirst()
                                    .map(h -> h.getExpirationDays())
                                    .orElse(null);
                        }
                    }

                    conversionDao.registerPixelConversion(loanApplication.getId(), FACEBOOK_SOURCE, FACEBOOK_SOME_REJECTION);

                    JSONObject json = new JSONObject();
                    if (rejectionExpirationDays == null || rejectionExpirationDays <= 90) {
                        json.put("type", "rechazoLeve90");
                    } else if (rejectionExpirationDays <= 180) {
                        json.put("type", "rechazoLeve180");
                    } else {
                        json.put("type", "rechazoGrave");
                    }
                    conversionsToSend.put(json);
                }
            }
        }


        return conversionsToSend;
    }

    public boolean hasSendConversion(LoanApplication loanApplication, String pixelEntiy, String instance) {
        List<Conversion> conversions = conversionDao.getConversions(loanApplication.getId());
        if (conversions.stream().anyMatch(c ->
                c.getEntity() != null && c.getEntity().equalsIgnoreCase(pixelEntiy)
                        && c.getInstance() != null && c.getInstance().equalsIgnoreCase(instance))) {
            return true;
        }
        return false;
    }

    public boolean passAcceso(int personId) throws Exception {
        StaticDBInfo staticDBInfo = personDao.getInfoFromStaticDB(personId);
        if (staticDBInfo != null && staticDBInfo.getRuc() != null) {
            Integer employeesQuantity = personDao.getEmployeesQuantity(staticDBInfo.getRuc());

            if (employeesQuantity != null && employeesQuantity >= 10)
                return true;
        }
        return false;
    }

    private void addEventToRegister(JSONArray conversionsToSend, String type, String value){
        JSONObject json = new JSONObject();
        json.put("type", type);
        if(value != null)
            json.put("value", value);
        conversionsToSend.put(json);
    }

}
