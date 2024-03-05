package com.affirm.common.service;

import com.affirm.common.model.Transaction;
import com.brsanthu.googleanalytics.ItemHit;
import com.brsanthu.googleanalytics.PageViewHit;
import com.brsanthu.googleanalytics.TransactionHit;

public interface GoogleAnalyticsService  {

    void Configure(String ip, String clientID, String userAgent, Integer countryId);

    void Configure(String ip, String clientID, String userAgent, String source, String medium, String campaign, Integer countryId);

    void sendTransaction(Transaction transaction);

    void sendPageView(String url);

    void sendPageView(String url, String title);

    void sendPageView(String url, String title, String description);

    PageViewHit convert(PageViewHit pageViewHit);

    ItemHit convert(ItemHit itemHit);

    TransactionHit convert(TransactionHit transactionHit);

    void sendEvent(String action);
}
