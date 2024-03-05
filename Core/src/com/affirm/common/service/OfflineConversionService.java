package com.affirm.common.service;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;

public interface OfflineConversionService {

    void sendOfflineConversion(Credit credit) throws Exception;

    void sendOfflineConversion(Credit credit, LoanApplication loanApplication, User user, Person person);

    byte[] getAdwordsOfflineConversionExcel(String token) throws Exception;

    void callGA4Event(LoanApplication loanApplication, String eventName, String ga4MeasurementId, String ga4ApiSecret);

    void callUniversalAnalyticsEvent(LoanApplication loanApplication, String eventCategory, String eventName, String trackingId);

    void callFacebookConversion(Credit credit, User user, LoanApplication loanApplication, Person person, String token, String eventName, String pixelId);
}
