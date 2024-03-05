package com.affirm.common.service.impl;

import com.affirm.common.model.Item;
import com.affirm.common.model.Transaction;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.GoogleAnalyticsService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import com.brsanthu.googleanalytics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service("googleAnalyticsService")
public class GoogleAnalyticsServiceImpl implements GoogleAnalyticsService{
    @Autowired
    private UtilService utilService;

    private GoogleAnalytics ga;
    private HttpServletRequest request;

    private String ip;
    private String clientID;

    private String source;
    private String medium;
    private String campaign;

    public GoogleAnalyticsServiceImpl() {
        ga = new GoogleAnalytics(Configuration.getGAKey(CountryParam.COUNTRY_PERU));
    }

    @Override
    public void Configure(String ip, String clientID, String userAgent, Integer countryId) {
        this.ip = ip;
        this.clientID = clientID;
        GoogleAnalyticsConfig config = new GoogleAnalyticsConfig().setUserAgent(userAgent);
        this.ga = new GoogleAnalytics(config, Configuration.getGAKey(countryId));
    }

    @Override
    public void Configure(String ip, String clientID, String userAgent, String source , String medium, String campaign, Integer countryId) {
        this.ip = ip;
        this.clientID = clientID;
        GoogleAnalyticsConfig config = new GoogleAnalyticsConfig().setUserAgent(userAgent);
        this.ga = new GoogleAnalytics(config, Configuration.getGAKey(countryId));
        this.source = source;
        this.medium = medium;
        this.campaign =campaign;
    }

    @Override
    public void sendTransaction(Transaction transaction) {
        for (Item product : transaction.getItems())
        {
            ItemHit item = new ItemHit().txId(product.getId())
                                        .itemName(product.getName())
                                        .itemCategory(product.getCategory())
                                        .itemCode(product.getCode())
                                        .itemPrice(product.getRevenue())
                                        .itemQuantity(product.getQuantity())
                                        .currencyCode("PEN");

            ga.post(convert(item));
        }

        TransactionHit transactionHit = convert(new TransactionHit(transaction.getId(), transaction.getAffiliation(),transaction.getRevenue()).txTax(transaction.getTax()).currencyCode("PEN"));

        ga.post(transactionHit);
    }

    @Override
    public void sendPageView(String url) {
        this.sendPageView(url, "", "");
    }

    @Override
    public void sendPageView(String url, String title) {
        this.sendPageView(url, title, "");
    }

    @Override
    public void sendPageView(String url, String title, String description) {
        ga.post(convert(new PageViewHit(url, title, description)));
    }

    @Override
    public PageViewHit convert(PageViewHit pageViewHit) {
        return pageViewHit.userIp(ip).clientId(clientID);
    }

    @Override
    public ItemHit convert(ItemHit itemHit) {

        itemHit.campaignName(this.campaign);
        itemHit.campaignSource(this.source);
        itemHit.campaignMedium(this.medium);

        return itemHit.userIp(ip).clientId(clientID);
    }

    @Override
    public TransactionHit convert(TransactionHit transactionHit) {

        transactionHit.campaignName(this.campaign);
        transactionHit.campaignSource(this.source);
        transactionHit.campaignMedium(this.medium);

        return transactionHit.userIp(ip).clientId(clientID);
    }

    @Override
    public void sendEvent(String action) {
        EventHit eventHit = new EventHit()
                .eventCategory("conversion")
                .eventAction(action)
                .clientId(this.clientID)
                .userIp(this.ip);

        ga.post(eventHit);
    }
}
