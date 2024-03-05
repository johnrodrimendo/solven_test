package com.affirm.client.model.messengerbot;

import com.affirm.client.model.messengerbot.profile.FbProfile;
import com.affirm.common.model.transactional.User;

import java.util.Locale;

/**
 * Created by jarmando on 04/12/16.
 */
public interface SessionData {
    //generic states
    String STATE_INIT = "zero";//do you want to see credit offers?//stores a placeholer to know if it is the first time or not
    String STATE_SILENT = "silent";//do you want to see credit offers?
    //Pre loan app states:
    String STATE_REASON = "reason";//What is the credit for?//stores reasonId and JSONProducts separated by ;
    String STATE_AMOUNT = "amount";//How much do you need?//stores amount
    String STATE_TERM = "term";//What is the term?//stores term (days or months)
    String STATE_DOC_TYPE = "doctype";//What is the doc type?//stores doctype
    String STATE_DOC_NUMBER = "docnumber";//what is the docnumber? (Are you sure?)//stores docnumber
    String STATE_DOC_NUMBER_SURE = "docnumbersure";

    //MODULARIZED
    //modules 1 global plus 1 per product
    String M_GLOBAL = "GLOBAL";
    String M_TRADITIONAL = "TRADITIONAL";
    String M_SHORT_TERM = "SHORT_TERM";
    String M_SALARY_ADVANCE = "SALARY_ADVANCE";//ACTIVE
    String M_AUTOS = "AUTOS";
    String M_DEBT_CONSOLIDATION = "DEBT_CONSOLIDATION";

    //SUFFIX
    String INIT = "-INIT";

    //STATE
    //global module
    String GLOBAL_NEW = M_GLOBAL + "-NEW";
    String GLOBAL_PRODUCT = M_GLOBAL + "-PRODUCT";
    String GLOBAL_CONTACT = M_GLOBAL + "-CONTACT";

    //traditional module
    String TRADITIONAL_REASON = M_TRADITIONAL + INIT;
    String TRADITIONAL_AMOUNT = M_TRADITIONAL + "-AMOUNT";
    String TRADITIONAL_TERM = M_TRADITIONAL + "-TERM";
    String TRADITIONAL_DOC_TYPE = M_TRADITIONAL + "-DOC_TYPE";
    String TRADITIONAL_DOC_NUMBER= M_TRADITIONAL + "-DOC_NUMBER";
    String TRADITIONAL_DOC_NUMBER_SURE= M_TRADITIONAL + "-DOC_NUMBER_SURE";
    String TRADITIONAL_CE_BIRTHDAY = M_TRADITIONAL + "-CE_BIRTHDAY";

    String TRADITIONAL_REGEN = M_TRADITIONAL + "-REGEN";

    //shot term module
    String SHORT_TERM_AMOUNT = M_SHORT_TERM + INIT;//goto, reply-term
    String SHORT_TERM_TERM = M_SHORT_TERM + "-TERM";//goto, reply-docType
    String SHORT_TERM_DOC_TYPE = M_SHORT_TERM + "-DOC_TYPE";//goto qr, reply-docNumber, pb-docNumber
    String SHORT_TERM_DOC_NUMBER= M_SHORT_TERM + "-DOC_NUMBER";//goto, reply-sure
    String SHORT_TERM_DOC_NUMBER_SURE= M_SHORT_TERM + "-DOC_NUMBER_SURE";//goto qr, reply-enc
    String SHORT_TERM_CE_BIRTHDAY = M_SHORT_TERM + "-CE_BIRTHDAY";

    String SHORT_TERM_REGEN = M_SHORT_TERM + "-REGEN";

    // advance module
    String SALARY_ADVANCE_HAS_EMAIL = M_SALARY_ADVANCE + "-HAS_EMAIL";//INIT;
    //  yes
    String SALARY_ADVANCE_EMAIL = M_SALARY_ADVANCE + INIT;// "-EMAIL";//end we sent you an email
    //  no
    String SALARY_ADVANCE_DOC_TYPE = M_SALARY_ADVANCE + "-DOC_TYPE";
    String SALARY_ADVANCE_DOC_NUMBER = M_SALARY_ADVANCE + "-DOC_NUMBER";
    String SALARY_ADVANCE_DOC_NUMBER_SURE = M_SALARY_ADVANCE + "-DOC_NUMBER_SURE";
    String SALARY_ADVANCE_CE_BIRTHDAY = M_SALARY_ADVANCE + "-CE_BIRTHDAY";

    String SALARY_ADVANCE_REGEN = M_SALARY_ADVANCE + "-REGEN";

    //autos module
    String AUTOS = M_AUTOS + INIT;

    //consolidation module
    String DEBT_CONSOLIDATION = M_DEBT_CONSOLIDATION + INIT;

    String M_CONSUMER_CREDIT = "CONSUMER_CREDIT";
    String CONSUMER_CREDIT_NEW = M_CONSUMER_CREDIT + "-NEW";
    String CONSUMER_CREDIT_DOCUMENT_TYPE = M_CONSUMER_CREDIT + "-DOCUMENT_TYPE";
    String CONSUMER_CREDIT_DOCUMENT_NUMBER_SURE = M_CONSUMER_CREDIT + "-DOCUMENT_NUMBER_SURE";
    String CONSUMER_CREDIT_DOCUMENT_NUMBER = M_CONSUMER_CREDIT + "-DOCUMENT_NUMBER";
    String CONSUMER_CREDIT_FACEBOOK_USER = M_CONSUMER_CREDIT + "-FACEBOOK_USER";
    String CONSUMER_CREDIT_CATEGORY_REASON = M_CONSUMER_CREDIT + "-CATEGORY-REASON";
    String CONSUMER_CREDIT_REASON = M_CONSUMER_CREDIT + "-REASON";
    String CONSUMER_CREDIT_TERM = M_CONSUMER_CREDIT + "-TERM";
    String CONSUMER_CREDIT_AMOUNT = M_CONSUMER_CREDIT + "-AMOUNT";
    String CONSUMER_CREDIT_FINISHED = M_CONSUMER_CREDIT + "-FINISHED";
    String CONSUMER_PICK_COUNTRY = M_CONSUMER_CREDIT + "-PICK_COUNTRY";

    String M_GENERAL = "GENERAL";
    String GENERAL_GET_STARTED   = M_GENERAL + "-LEAVE_MESSAGE";
    String GENERAL_LEAVE_MESSAGE = M_GENERAL + "-LEAVE_MESSAGE";

    void clearAnswers();
    User getUser();
    void setUser(User user);
    boolean isPresentAnswer(String state);
    boolean isLocaleSupported();
    Locale getLocale();
    void setLocale(Locale locale);
    FbProfile getProfile();
    String getAnswer(String state);
    void setCurrentAnswer(String answer);
    void setAnswer(String state, String answer);
    String getCurrentState();
    void setCurrentState(String newState);
    String getPreviousState();

    void setIsGlobal(boolean isGlobal);
    boolean isGlobal();

    static String getModule(String destinyState) {
        return destinyState.split("-")[0];
    }
}