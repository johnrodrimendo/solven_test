package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.*;

public class EntityExtranetConfiguration implements Serializable {

    private String faviconUrl;

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public void setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
    }

    private String loginLogoImg;
    private String loginBackground;
    private String loginButtonBackgroundColor;
    private String loginButtonTextColor;
    private String loginLinkTextColor;
    private String loginInputBackgroundColor;
    private String loginInputTextColor;
    private String alertTextColor;
    private String alertButtonBackgroundColor;
    private String alertButtonTextColor;
    private String alertCancelButtonBackgroundColor;
    private String alertCancelButtonTextColor;
    private String headerLogoImg;
    private String headerBackgroundColor;
    private String headerBackgroundHoverColor;
    private String headerBackgroundTextHoverColor;
    private String headerTextColor;
    private String menuTitle;
    private String menuTitleTextColor;
    private String menuActiveTextColor;
    private String menuHoverBackgroundColor;
    private String menuBackgroundColor;
    private String generalBackgroundColor;
    private String cardBackgroundColor;
    private String cardBorderColor;
    private String cardTitleColor;
    private String cardTableHeaderBackgroundColor;
    private String cardTableHeaderTextColor;
    private String cardTableBodyBackgroundColor;
    private String cardTableBodyHoverBackgroundColor;
    private String cardTableBodyTextColor;
    private String cardTableBodyLinkColor;
    private String cardTablePaginatorTextColor;
    private String cardTablePaginatorBackground;
    private String cardTablePaginatorBackgroundHover;
    private String cardTablePaginatorTextHoverColor;
    private String cardTablePaginatorActiveBackground;
    private String cardFilterLabelTextColor;
    private String cardFilterInputTextColor;
    private String cardFilterInputBackgroundColor;
    private String cardFilterInputBorderColor;
    private String cardFilterInputFocusBorderColor;
    private String cardFilterButtonBackgroundColor;
    private String cardFilterButtonTextColor;
    private String cardTableBorderColor;
    private String selectLinkArrow;
    private String backgroundEmailUrl;
    private Boolean showFooterEmail;
    private String agentImgEmailUrl;
    private String agentEmailName;
    private String resetPasswordTextEmail;
    private String backgroundColorButtonEmail;
    private String backgroundColorButtonCancelModal;
    private String textColorButtonCancelModal;
    private String backgroundColorButtonAcceptModal;
    private String textColorButtonAcceptModal;
    private String backgroundColorButtonDropdown;
    private String textColorButtonDropdown;
    private String backgroundColorButtonCloseModal;
    private String textColorButtonCloseModal;
    private String backgroundColorButtonTableFirstAction;
    private String textColorButtonTableFirstAction;
    private String backgroundColorButtonTableSecondAction;
    private String textColorButtonTableSecondAction;
    private List<FunnelConfiguration> funnelConfiguration;
    private DisburseCreditPageConfiguration disburseCreditPageConfiguration;
    private DisbursedCreditPageConfiguration disbursedCreditPageConfiguration;
    private InProcessCreditPageConfiguration inProcessCreditPageConfiguration;
    private CallCenterPageConfiguration callCenterPageConfiguration;
    private ToVerifyPageConfiguration toVerifyPageConfiguration;
    private CreateCreditPageConfiguration createCreditPageConfiguration;
    private RejectedPageConfiguration rejectedPageConfiguration;
    private EvaluationPageConfiguration evaluationPageConfiguration;
    private PaymentCommitmentPageConfiguration paymentCommitmentPageConfiguration;
    //NEW
    private String loanProgressColor;
    private String offerActiveColor;
    private String offerActiveButtonColor;
    private String offerActiveButtonBackgroundColor;
    private String summaryEvaluationBackgroundColor;
    private String panelActiveColor;
    private String tabActiveColor;
    private String buttonActionBackgroundColor;
    private String buttonActionTextColor;

    private String buttonActionPrincipalBackgroundColor;
    private String buttonActionPrincipalTextColor;
    private String buttonActionSecondaryBackgroundColor;
    private String buttonActionSecondaryTextColor;

    private List<Integer> professionOccupationIds;
    private List<Integer> occupationIds;

    private MarketingCampaignConfiguration marketingCampaignConfiguration;

    public String getBackgroundColorButtonTableFirstAction() {
        return backgroundColorButtonTableFirstAction;
    }

    public void setBackgroundColorButtonTableFirstAction(String backgroundColorButtonTableFirstAction) {
        this.backgroundColorButtonTableFirstAction = backgroundColorButtonTableFirstAction;
    }

    public String getTextColorButtonTableFirstAction() {
        return textColorButtonTableFirstAction;
    }

    public void setTextColorButtonTableFirstAction(String textColorButtonTableFirstAction) {
        this.textColorButtonTableFirstAction = textColorButtonTableFirstAction;
    }

    public String getBackgroundColorButtonTableSecondAction() {
        return backgroundColorButtonTableSecondAction;
    }

    public void setBackgroundColorButtonTableSecondAction(String backgroundColorButtonTableSecondAction) {
        this.backgroundColorButtonTableSecondAction = backgroundColorButtonTableSecondAction;
    }

    public String getTextColorButtonTableSecondAction() {
        return textColorButtonTableSecondAction;
    }

    public void setTextColorButtonTableSecondAction(String textColorButtonTableSecondAction) {
        this.textColorButtonTableSecondAction = textColorButtonTableSecondAction;
    }

    public String getBackgroundColorButtonCloseModal() {
        return backgroundColorButtonCloseModal;
    }

    public void setBackgroundColorButtonCloseModal(String backgroundColorButtonCloseModal) {
        this.backgroundColorButtonCloseModal = backgroundColorButtonCloseModal;
    }

    public String getTextColorButtonCloseModal() {
        return textColorButtonCloseModal;
    }

    public void setTextColorButtonCloseModal(String textColorButtonCloseModal) {
        this.textColorButtonCloseModal = textColorButtonCloseModal;
    }

    public String getBackgroundColorButtonCancelModal() {
        return backgroundColorButtonCancelModal;
    }

    public void setBackgroundColorButtonCancelModal(String backgroundColorButtonCancelModal) {
        this.backgroundColorButtonCancelModal = backgroundColorButtonCancelModal;
    }

    public String getTextColorButtonCancelModal() {
        return textColorButtonCancelModal;
    }

    public void setTextColorButtonCancelModal(String textColorButtonCancelModal) {
        this.textColorButtonCancelModal = textColorButtonCancelModal;
    }

    public String getBackgroundColorButtonAcceptModal() {
        return backgroundColorButtonAcceptModal;
    }

    public void setBackgroundColorButtonAcceptModal(String backgroundColorButtonAcceptModal) {
        this.backgroundColorButtonAcceptModal = backgroundColorButtonAcceptModal;
    }

    public String getTextColorButtonAcceptModal() {
        return textColorButtonAcceptModal;
    }

    public void setTextColorButtonAcceptModal(String textColorButtonAcceptModal) {
        this.textColorButtonAcceptModal = textColorButtonAcceptModal;
    }

    public String getBackgroundColorButtonDropdown() {
        return backgroundColorButtonDropdown;
    }

    public void setBackgroundColorButtonDropdown(String backgroundColorButtonDropdown) {
        this.backgroundColorButtonDropdown = backgroundColorButtonDropdown;
    }

    public String getTextColorButtonDropdown() {
        return textColorButtonDropdown;
    }

    public void setTextColorButtonDropdown(String textColorButtonDropdown) {
        this.textColorButtonDropdown = textColorButtonDropdown;
    }

    public String getAgentEmailName() {
        return agentEmailName;
    }

    public void setAgentEmailName(String agentEmailName) {
        this.agentEmailName = agentEmailName;
    }

    public String getCardTablePaginatorBackground() {
        return cardTablePaginatorBackground;
    }

    public void setCardTablePaginatorBackground(String cardTablePaginatorBackgroundColor) {
        this.cardTablePaginatorBackground = cardTablePaginatorBackgroundColor;
    }

    public String getCardTablePaginatorBackgroundHover() {
        return cardTablePaginatorBackgroundHover;
    }

    public void setCardTablePaginatorBackgroundHover(String cardTablePaginatorBackgroundHoverColor) {
        this.cardTablePaginatorBackgroundHover = cardTablePaginatorBackgroundHoverColor;
    }

    public String getCardTablePaginatorTextHoverColor() {
        return cardTablePaginatorTextHoverColor;
    }

    public void setCardTablePaginatorTextHoverColor(String cardTablePaginatorTextHoverColor) {
        this.cardTablePaginatorTextHoverColor = cardTablePaginatorTextHoverColor;
    }

    public String getCardTablePaginatorActiveBackground() {
        return cardTablePaginatorActiveBackground;
    }

    public void setCardTablePaginatorActiveBackground(String cardTablePaginatorActiveBackgroundColor) {
        this.cardTablePaginatorActiveBackground = cardTablePaginatorActiveBackgroundColor;
    }

    public String getBackgroundColorButtonEmail() {
        return backgroundColorButtonEmail;
    }

    public void setBackgroundColorButtonEmail(String backgroundColorButtonEmail) {
        this.backgroundColorButtonEmail = backgroundColorButtonEmail;
    }

    public String getResetPasswordTextEmail() {
        return resetPasswordTextEmail;
    }

    public void setResetPasswordTextEmail(String resetPasswordTextEmail) {
        this.resetPasswordTextEmail = resetPasswordTextEmail;
    }

    public String getAgentImgEmailUrl() {
        return agentImgEmailUrl;
    }

    public void setAgentImgEmailUrl(String agentImgEmailUrl) {
        this.agentImgEmailUrl = agentImgEmailUrl;
    }

    public String getBackgroundEmailUrl() {
        return backgroundEmailUrl;
    }

    public void setBackgroundEmailUrl(String backgroundEmailUrl) {
        this.backgroundEmailUrl = backgroundEmailUrl;
    }

    public Boolean getShowFooterEmail() {
        return showFooterEmail;
    }

    public void setShowFooterEmail(Boolean showFooterEmail) {
        this.showFooterEmail = showFooterEmail;
    }

    public String getHeaderBackgroundHoverColor() {
        return headerBackgroundHoverColor;
    }

    public void setHeaderBackgroundHoverColor(String headerBackgroundHoverColor) {
        this.headerBackgroundHoverColor = headerBackgroundHoverColor;
    }

    public String getHeaderBackgroundTextHoverColor() {
        return headerBackgroundTextHoverColor;
    }

    public void setHeaderBackgroundTextHoverColor(String headerBackgroundTextHoverColor) {
        this.headerBackgroundTextHoverColor = headerBackgroundTextHoverColor;
    }

    public String getLoginLogoImg() {
        return loginLogoImg;
    }

    public void setLoginLogoImg(String loginLogoImg) {
        this.loginLogoImg = loginLogoImg;
    }

    public String getLoginBackground() {
        return loginBackground;
    }

    public void setLoginBackground(String loginBackground) {
        this.loginBackground = loginBackground;
    }

    public String getLoginButtonBackgroundColor() {
        return loginButtonBackgroundColor;
    }

    public void setLoginButtonBackgroundColor(String loginButtonBackgroundColor) {
        this.loginButtonBackgroundColor = loginButtonBackgroundColor;
    }

    public String getLoginButtonTextColor() {
        return loginButtonTextColor;
    }

    public void setLoginButtonTextColor(String loginButtonTextColor) {
        this.loginButtonTextColor = loginButtonTextColor;
    }

    public String getLoginLinkTextColor() {
        return loginLinkTextColor;
    }

    public void setLoginLinkTextColor(String loginLinkTextColor) {
        this.loginLinkTextColor = loginLinkTextColor;
    }

    public String getLoginInputBackgroundColor() {
        return loginInputBackgroundColor;
    }

    public void setLoginInputBackgroundColor(String loginInputBackgroundColor) {
        this.loginInputBackgroundColor = loginInputBackgroundColor;
    }

    public String getLoginInputTextColor() {
        return loginInputTextColor;
    }

    public void setLoginInputTextColor(String loginInputTextColor) {
        this.loginInputTextColor = loginInputTextColor;
    }

    public String getAlertTextColor() {
        return alertTextColor;
    }

    public void setAlertTextColor(String alertTextColor) {
        this.alertTextColor = alertTextColor;
    }

    public String getAlertButtonBackgroundColor() {
        return alertButtonBackgroundColor;
    }

    public void setAlertButtonBackgroundColor(String alertButtonBackgroundColor) {
        this.alertButtonBackgroundColor = alertButtonBackgroundColor;
    }

    public String getAlertButtonTextColor() {
        return alertButtonTextColor;
    }

    public void setAlertButtonTextColor(String alertButtonTextColor) {
        this.alertButtonTextColor = alertButtonTextColor;
    }

    public String getHeaderLogoImg() {
        return headerLogoImg;
    }

    public void setHeaderLogoImg(String headerLogoImg) {
        this.headerLogoImg = headerLogoImg;
    }

    public String getHeaderBackgroundColor() {
        return headerBackgroundColor;
    }

    public void setHeaderBackgroundColor(String headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
    }

    public String getHeaderTextColor() {
        return headerTextColor;
    }

    public void setHeaderTextColor(String headerTextColor) {
        this.headerTextColor = headerTextColor;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public String getMenuTitleTextColor() {
        return menuTitleTextColor;
    }

    public void setMenuTitleTextColor(String menuTitleTextColor) {
        this.menuTitleTextColor = menuTitleTextColor;
    }

    public String getMenuActiveTextColor() {
        return menuActiveTextColor;
    }

    public void setMenuActiveTextColor(String menuActiveTextColor) {
        this.menuActiveTextColor = menuActiveTextColor;
    }

    public String getMenuHoverBackgroundColor() {
        return menuHoverBackgroundColor;
    }

    public void setMenuHoverBackgroundColor(String menuHoverBackgroundColor) {
        this.menuHoverBackgroundColor = menuHoverBackgroundColor;
    }

    public String getMenuBackgroundColor() {
        return menuBackgroundColor;
    }

    public void setMenuBackgroundColor(String menuBackgroundColor) {
        this.menuBackgroundColor = menuBackgroundColor;
    }

    public String getGeneralBackgroundColor() {
        return generalBackgroundColor;
    }

    public void setGeneralBackgroundColor(String generalBackgroundColor) {
        this.generalBackgroundColor = generalBackgroundColor;
    }

    public String getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public void setCardBackgroundColor(String cardBackgroundColor) {
        this.cardBackgroundColor = cardBackgroundColor;
    }

    public String getCardBorderColor() {
        return cardBorderColor;
    }

    public void setCardBorderColor(String cardBorderColor) {
        this.cardBorderColor = cardBorderColor;
    }

    public String getCardTitleColor() {
        return cardTitleColor;
    }

    public void setCardTitleColor(String cardTitleColor) {
        this.cardTitleColor = cardTitleColor;
    }

    public String getCardTableHeaderBackgroundColor() {
        return cardTableHeaderBackgroundColor;
    }

    public void setCardTableHeaderBackgroundColor(String cardTableHeaderBackgroundColor) {
        this.cardTableHeaderBackgroundColor = cardTableHeaderBackgroundColor;
    }

    public String getCardTableHeaderTextColor() {
        return cardTableHeaderTextColor;
    }

    public void setCardTableHeaderTextColor(String cardTableHeaderTextColor) {
        this.cardTableHeaderTextColor = cardTableHeaderTextColor;
    }

    public String getCardTableBodyBackgroundColor() {
        return cardTableBodyBackgroundColor;
    }

    public void setCardTableBodyBackgroundColor(String cardTableBodyBackgroundColor) {
        this.cardTableBodyBackgroundColor = cardTableBodyBackgroundColor;
    }

    public String getCardTableBodyHoverBackgroundColor() {
        return cardTableBodyHoverBackgroundColor;
    }

    public void setCardTableBodyHoverBackgroundColor(String cardTableBodyHoverBackgroundColor) {
        this.cardTableBodyHoverBackgroundColor = cardTableBodyHoverBackgroundColor;
    }

    public String getCardTableBodyTextColor() {
        return cardTableBodyTextColor;
    }

    public void setCardTableBodyTextColor(String cardTableBodyTextColor) {
        this.cardTableBodyTextColor = cardTableBodyTextColor;
    }

    public String getCardTableBodyLinkColor() {
        return cardTableBodyLinkColor;
    }

    public void setCardTableBodyLinkColor(String cardTableBodyLinkColor) {
        this.cardTableBodyLinkColor = cardTableBodyLinkColor;
    }

    public String getCardTablePaginatorTextColor() {
        return cardTablePaginatorTextColor;
    }

    public void setCardTablePaginatorTextColor(String cardTablePaginatorTextColor) {
        this.cardTablePaginatorTextColor = cardTablePaginatorTextColor;
    }

    public String getCardFilterLabelTextColor() {
        return cardFilterLabelTextColor;
    }

    public void setCardFilterLabelTextColor(String cardFilterLabelTextColor) {
        this.cardFilterLabelTextColor = cardFilterLabelTextColor;
    }

    public String getCardFilterInputTextColor() {
        return cardFilterInputTextColor;
    }

    public void setCardFilterInputTextColor(String cardFilterInputTextColor) {
        this.cardFilterInputTextColor = cardFilterInputTextColor;
    }

    public String getCardFilterInputBackgroundColor() {
        return cardFilterInputBackgroundColor;
    }

    public void setCardFilterInputBackgroundColor(String cardFilterInputBackgroundColor) {
        this.cardFilterInputBackgroundColor = cardFilterInputBackgroundColor;
    }

    public String getCardFilterInputBorderColor() {
        return cardFilterInputBorderColor;
    }

    public void setCardFilterInputBorderColor(String cardFilterInputBorderColor) {
        this.cardFilterInputBorderColor = cardFilterInputBorderColor;
    }

    public String getCardFilterInputFocusBorderColor() {
        return cardFilterInputFocusBorderColor;
    }

    public void setCardFilterInputFocusBorderColor(String cardFilterInputFocusBorderColor) {
        this.cardFilterInputFocusBorderColor = cardFilterInputFocusBorderColor;
    }

    public String getCardFilterButtonBackgroundColor() {
        return cardFilterButtonBackgroundColor;
    }

    public void setCardFilterButtonBackgroundColor(String cardFilterButtonBackgroundColor) {
        this.cardFilterButtonBackgroundColor = cardFilterButtonBackgroundColor;
    }

    public String getCardFilterButtonTextColor() {
        return cardFilterButtonTextColor;
    }

    public void setCardFilterButtonTextColor(String cardFilterButtonTextColor) {
        this.cardFilterButtonTextColor = cardFilterButtonTextColor;
    }

    public String getCardTableBorderColor() {
        return cardTableBorderColor;
    }

    public void setCardTableBorderColor(String cardTableBorderColor) {
        this.cardTableBorderColor = cardTableBorderColor;
    }

    public String getSelectLinkArrow() {
        return selectLinkArrow;
    }

    public void setSelectLinkArrow(String selectLinkArrow) {
        this.selectLinkArrow = selectLinkArrow;
    }

    public List<FunnelConfiguration> getFunnelConfiguration() {
        return funnelConfiguration;
    }

    public void setFunnelConfiguration(List<FunnelConfiguration> funnelConfiguration) {
        this.funnelConfiguration = funnelConfiguration;
    }

    public DisburseCreditPageConfiguration getDisburseCreditPageConfiguration() {
        return disburseCreditPageConfiguration;
    }

    public void setDisburseCreditPageConfiguration(DisburseCreditPageConfiguration disburseCreditPageConfiguration) {
        this.disburseCreditPageConfiguration = disburseCreditPageConfiguration;
    }

    public DisbursedCreditPageConfiguration getDisbursedCreditPageConfiguration() {
        return disbursedCreditPageConfiguration;
    }

    public void setDisbursedCreditPageConfiguration(DisbursedCreditPageConfiguration disbursedCreditPageConfiguration) {
        this.disbursedCreditPageConfiguration = disbursedCreditPageConfiguration;
    }

    public InProcessCreditPageConfiguration getInProcessCreditPageConfiguration() {
        return inProcessCreditPageConfiguration;
    }

    public void setInProcessCreditPageConfiguration(InProcessCreditPageConfiguration inProcessCreditPageConfiguration) {
        this.inProcessCreditPageConfiguration = inProcessCreditPageConfiguration;
    }

    public CallCenterPageConfiguration getCallCenterPageConfiguration() {
        return callCenterPageConfiguration;
    }

    public void setCallCenterPageConfiguration(CallCenterPageConfiguration callCenterPageConfiguration) {
        this.callCenterPageConfiguration = callCenterPageConfiguration;
    }

    public ToVerifyPageConfiguration getToVerifyPageConfiguration() {
        return toVerifyPageConfiguration;
    }

    public void setToVerifyPageConfiguration(ToVerifyPageConfiguration toVerifyPageConfiguration) {
        this.toVerifyPageConfiguration = toVerifyPageConfiguration;
    }

    public CreateCreditPageConfiguration getCreateCreditPageConfiguration() {
        return createCreditPageConfiguration;
    }

    public void setCreateCreditPageConfiguration(CreateCreditPageConfiguration createCreditPageConfiguration) {
        this.createCreditPageConfiguration = createCreditPageConfiguration;
    }

    public String getAlertCancelButtonBackgroundColor() {
        return alertCancelButtonBackgroundColor;
    }

    public void setAlertCancelButtonBackgroundColor(String alertCancelButtonBackgroundColor) {
        this.alertCancelButtonBackgroundColor = alertCancelButtonBackgroundColor;
    }

    public String getAlertCancelButtonTextColor() {
        return alertCancelButtonTextColor;
    }

    public void setAlertCancelButtonTextColor(String alertCancelButtonTextColor) {
        this.alertCancelButtonTextColor = alertCancelButtonTextColor;
    }

    public RejectedPageConfiguration getRejectedPageConfiguration() {
        return rejectedPageConfiguration;
    }

    public void setRejectedPageConfiguration(RejectedPageConfiguration rejectedPageConfiguration) {
        this.rejectedPageConfiguration = rejectedPageConfiguration;
    }

    public EvaluationPageConfiguration getEvaluationPageConfiguration() {
        return evaluationPageConfiguration;
    }

    public void setEvaluationPageConfiguration(EvaluationPageConfiguration evaluationPageConfiguration) {
        this.evaluationPageConfiguration = evaluationPageConfiguration;
    }

    public MarketingCampaignConfiguration getMarketingCampaignConfiguration() {
        return marketingCampaignConfiguration;
    }

    public void setMarketingCampaignConfiguration(MarketingCampaignConfiguration marketingCampaignConfiguration) {
        this.marketingCampaignConfiguration = marketingCampaignConfiguration;
    }

    public EntityExtranetConfiguration() {

    }

    public void fillFromDb(JSONObject json) throws Exception {
        setFaviconUrl(JsonUtil.getStringFromJson(json, "faviconUrl", null));
        setLoginLogoImg(JsonUtil.getStringFromJson(json, "loginLogoImg", null));
        setLoginBackground(JsonUtil.getStringFromJson(json, "loginBackground", null));
        setLoginButtonBackgroundColor(JsonUtil.getStringFromJson(json, "loginButtonBackgroundColor", null));
        setLoginButtonTextColor(JsonUtil.getStringFromJson(json, "loginButtonTextColor", null));
        setLoginLinkTextColor(JsonUtil.getStringFromJson(json, "loginLinkTextColor", null));
        setLoginInputBackgroundColor(JsonUtil.getStringFromJson(json, "loginInputBackgroundColor", null));
        setLoginInputTextColor(JsonUtil.getStringFromJson(json, "loginInputTextColor", null));
        setAlertTextColor(JsonUtil.getStringFromJson(json, "alertTextColor", null));
        setAlertButtonBackgroundColor(JsonUtil.getStringFromJson(json, "alertButtonBackgroundColor", null));
        setAlertButtonTextColor(JsonUtil.getStringFromJson(json, "alertButtonTextColor", null));
        setHeaderLogoImg(JsonUtil.getStringFromJson(json, "headerLogoImg", null));
        setHeaderBackgroundColor(JsonUtil.getStringFromJson(json, "headerBackgroundColor", null));
        setHeaderTextColor(JsonUtil.getStringFromJson(json, "headerTextColor", null));
        setMenuTitle(JsonUtil.getStringFromJson(json, "menuTitle", null));
        setMenuTitleTextColor(JsonUtil.getStringFromJson(json, "menuTitleTextColor", null));
        setMenuActiveTextColor(JsonUtil.getStringFromJson(json, "menuActiveTextColor", null));
        setMenuHoverBackgroundColor(JsonUtil.getStringFromJson(json, "menuHoverBackgroundColor", null));
        setMenuBackgroundColor(JsonUtil.getStringFromJson(json, "menuBackgroundColor", null));
        setGeneralBackgroundColor(JsonUtil.getStringFromJson(json, "generalBackgroundColor", null));
        setCardBackgroundColor(JsonUtil.getStringFromJson(json, "cardBackgroundColor", null));
        setCardBorderColor(JsonUtil.getStringFromJson(json, "cardBorderColor", null));
        setCardTitleColor(JsonUtil.getStringFromJson(json, "cardTitleColor", null));
        setCardTableHeaderBackgroundColor(JsonUtil.getStringFromJson(json, "cardTableHeaderBackgroundColor", null));
        setCardTableHeaderTextColor(JsonUtil.getStringFromJson(json, "cardTableHeaderTextColor", null));
        setCardTableBodyBackgroundColor(JsonUtil.getStringFromJson(json, "cardTableBodyBackgroundColor", null));
        setCardTableBodyHoverBackgroundColor(JsonUtil.getStringFromJson(json, "cardTableBodyHoverBackgroundColor", null));
        setCardTableBodyTextColor(JsonUtil.getStringFromJson(json, "cardTableBodyTextColor", null));
        setCardTableBodyLinkColor(JsonUtil.getStringFromJson(json, "cardTableBodyLinkColor", null));
        setCardTablePaginatorTextColor(JsonUtil.getStringFromJson(json, "cardTablePaginatorTextColor", null));
        setCardFilterLabelTextColor(JsonUtil.getStringFromJson(json, "cardFilterLabelTextColor", null));
        setCardFilterInputTextColor(JsonUtil.getStringFromJson(json, "cardFilterInputTextColor", null));
        setCardFilterInputBackgroundColor(JsonUtil.getStringFromJson(json, "cardFilterInputBackgroundColor", null));
        setCardFilterInputBorderColor(JsonUtil.getStringFromJson(json, "cardFilterInputBorderColor", null));
        setCardFilterInputFocusBorderColor(JsonUtil.getStringFromJson(json, "cardFilterInputFocusBorderColor", null));
        setCardFilterButtonBackgroundColor(JsonUtil.getStringFromJson(json, "cardFilterButtonBackgroundColor", null));
        setCardFilterButtonTextColor(JsonUtil.getStringFromJson(json, "cardFilterButtonTextColor", null));
        setCardTableBorderColor(JsonUtil.getStringFromJson(json, "cardTableBorderColor", null));
        setSelectLinkArrow(JsonUtil.getStringFromJson(json, "selectLinkArrow", null));
        setHeaderBackgroundHoverColor(JsonUtil.getStringFromJson(json, "headerBackgroundHoverColor", null));
        setHeaderBackgroundTextHoverColor(JsonUtil.getStringFromJson(json, "headerBackgroundTextHoverColor", null));
        setBackgroundEmailUrl(JsonUtil.getStringFromJson(json, "backgroundEmailUrl", null));
        setShowFooterEmail(JsonUtil.getBooleanFromJson(json, "showFooterEmail", false));
        setAgentImgEmailUrl(JsonUtil.getStringFromJson(json, "agentImgEmailUrl", null));
        setResetPasswordTextEmail(JsonUtil.getStringFromJson(json, "resetPasswordTextEmail", null));
        setBackgroundColorButtonEmail(JsonUtil.getStringFromJson(json, "backgroundColorButtonEmail", null));
        setCardTablePaginatorBackground(JsonUtil.getStringFromJson(json, "cardTablePaginatorBackground", null));
        setCardTablePaginatorBackgroundHover(JsonUtil.getStringFromJson(json, "cardTablePaginatorBackgroundHover", null));
        setCardTablePaginatorTextHoverColor(JsonUtil.getStringFromJson(json, "cardTablePaginatorTextHoverColor", null));
        setCardTablePaginatorActiveBackground(JsonUtil.getStringFromJson(json, "cardTablePaginatorActiveBackground", null));
        setBackgroundColorButtonCancelModal(JsonUtil.getStringFromJson(json, "backgroundColorButtonCancelModal", null));
        setTextColorButtonCancelModal(JsonUtil.getStringFromJson(json, "textColorButtonCancelModal", null));
        setBackgroundColorButtonAcceptModal(JsonUtil.getStringFromJson(json, "backgroundColorButtonAcceptModal", null));
        setTextColorButtonAcceptModal(JsonUtil.getStringFromJson(json, "textColorButtonAcceptModal", null));
        setBackgroundColorButtonDropdown(JsonUtil.getStringFromJson(json, "backgroundColorButtonDropdown", null));
        setTextColorButtonDropdown(JsonUtil.getStringFromJson(json, "textColorButtonDropdown", null));
        setBackgroundColorButtonCloseModal(JsonUtil.getStringFromJson(json, "backgroundColorButtonCloseModal", null));
        setBackgroundColorButtonTableFirstAction(JsonUtil.getStringFromJson(json, "backgroundColorButtonTableFirstAction", null));
        setTextColorButtonTableFirstAction(JsonUtil.getStringFromJson(json, "textColorButtonTableFirstAction", null));
        setBackgroundColorButtonTableSecondAction(JsonUtil.getStringFromJson(json, "backgroundColorButtonTableSecondAction", null));
        setTextColorButtonTableSecondAction(JsonUtil.getStringFromJson(json, "textColorButtonTableSecondAction", null));
        if(JsonUtil.getJsonArrayFromJson(json, "funnelConfiguration", null) != null) setFunnelConfiguration(Arrays.asList(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "funnelConfiguration", null) .toString(), FunnelConfiguration[].class)));
        if(JsonUtil.getJsonObjectFromJson(json, "disburseCreditPageConfiguration", null) != null) setDisburseCreditPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "disburseCreditPageConfiguration", null) .toString(), DisburseCreditPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "disbursedCreditPageConfiguration", null) != null) setDisbursedCreditPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "disbursedCreditPageConfiguration", null) .toString(), DisbursedCreditPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "inProcessCreditPageConfiguration", null) != null) setInProcessCreditPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "inProcessCreditPageConfiguration", null) .toString(), InProcessCreditPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "callCenterPageConfiguration", null) != null) setCallCenterPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "callCenterPageConfiguration", null) .toString(), CallCenterPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "toVerifyPageConfiguration", null) != null) setToVerifyPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "toVerifyPageConfiguration", null) .toString(), ToVerifyPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "createCreditPageConfiguration", null) != null) setCreateCreditPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "createCreditPageConfiguration", null) .toString(), CreateCreditPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "rejectedPageConfiguration", null) != null) setRejectedPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "rejectedPageConfiguration", null) .toString(), RejectedPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "evaluationPageConfiguration", null) != null) setEvaluationPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "evaluationPageConfiguration", null) .toString(), EvaluationPageConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "paymentCommitmentPageConfiguration", null) != null) setPaymentCommitmentPageConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "paymentCommitmentPageConfiguration", null) .toString(), PaymentCommitmentPageConfiguration.class));
        setAlertCancelButtonBackgroundColor(JsonUtil.getStringFromJson(json, "alertCancelButtonBackgroundColor", null));
        setAlertButtonTextColor(JsonUtil.getStringFromJson(json, "alertCancelButtonTextColor", null));
        if(JsonUtil.getJsonArrayFromJson(json, "professionOccupationIds", null) != null) setProfessionOccupationIds(Arrays.asList(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "professionOccupationIds", null) .toString(), Integer[].class)));
        if(JsonUtil.getJsonArrayFromJson(json, "occupationIds", null) != null) setOccupationIds(Arrays.asList(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "occupationIds", null) .toString(), Integer[].class)));
        if(JsonUtil.getJsonObjectFromJson(json, "marketingCampaignConfiguration", null) != null) setMarketingCampaignConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "marketingCampaignConfiguration", null) .toString(), MarketingCampaignConfiguration.class));
    }

    public void fillDefaultValues(MessageSource messageSource, Locale locale) {

    }

    public static class MarketingCampaignConfiguration{


        private String marketingCampaignAwsTemplate;
        private String email;
        private String senderName;
        private List<MarketingCampaignConfigurationLog> logs = new ArrayList<>();
        private List<MarketingCampaignConfigurationAvailableType> availableTypes;
        private Double smsSolvenCost;
        private Double smsOwnCost;
        private Double emailSolvenCost;

        public String getMarketingCampaignAwsTemplate() {
            return marketingCampaignAwsTemplate;
        }

        public void setMarketingCampaignAwsTemplate(String marketingCampaignAwsTemplate) {
            this.marketingCampaignAwsTemplate = marketingCampaignAwsTemplate;
        }

        public List<MarketingCampaignConfigurationAvailableType> getAvailableTypes() {
            return availableTypes;
        }

        public void setAvailableTypes(List<MarketingCampaignConfigurationAvailableType> availableTypes) {
            this.availableTypes = availableTypes;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public List<MarketingCampaignConfigurationLog> getLogs() {
            return logs;
        }

        public void setLogs(List<MarketingCampaignConfigurationLog> logs) {
            this.logs = logs;
        }

        public Double getSmsSolvenCost() {
            return smsSolvenCost;
        }

        public void setSmsSolvenCost(Double smsSolvenCost) {
            this.smsSolvenCost = smsSolvenCost;
        }

        public Double getSmsOwnCost() {
            return smsOwnCost;
        }

        public void setSmsOwnCost(Double smsOwnCost) {
            this.smsOwnCost = smsOwnCost;
        }

        public Double getEmailSolvenCost() {
            return emailSolvenCost;
        }

        public void setEmailSolvenCost(Double emailSolvenCost) {
            this.emailSolvenCost = emailSolvenCost;
        }
    }

    public static class MarketingCampaignConfigurationLog{

        public static final char SERVICE_TYPE_SOLVEN = 'S';
        public static final char SERVICE_TYPE_OWN = 'O';
        public static final char SENDING_TYPE_ON_DEMAND = 'O';
        public static final char SENDING_TYPE_FOLLOW_UP = 'F';

        private String email;
        private String senderName;
        private Date updateTime = new Date();

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class MarketingCampaignConfigurationAvailableType{

        public static final String EMAIL_TYPE = "email";
        public static final String SMS_TYPE = "sms";

        public static final char SERVICE_TYPE_SOLVEN = 'S';
        public static final char SERVICE_TYPE_OWN = 'O';

        private String type;
        private String provider;
        private Character serviceType;
        private Boolean sendingTypeOnDemand;
        private Boolean sendingTypeFollowUp;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Character getServiceType() {
            return serviceType;
        }

        public void setServiceType(Character serviceType) {
            this.serviceType = serviceType;
        }

        public Boolean getSendingTypeOnDemand() {
            return sendingTypeOnDemand;
        }

        public void setSendingTypeOnDemand(Boolean sendingTypeOnDemand) {
            this.sendingTypeOnDemand = sendingTypeOnDemand;
        }

        public Boolean getSendingTypeFollowUp() {
            return sendingTypeFollowUp;
        }

        public void setSendingTypeFollowUp(Boolean sendingTypeFollowUp) {
            this.sendingTypeFollowUp = sendingTypeFollowUp;
        }
    }

    public static class FunnelConfiguration{
        private Integer productCategoryId;
        private String filterFragment;
        private List<FunnelStep> steps = new ArrayList<>();

        public Integer getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(Integer productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public String getFilterFragment() {
            return filterFragment;
        }

        public void setFilterFragment(String filterFragment) {
            this.filterFragment = filterFragment;
        }

        public List<FunnelStep> getSteps() {
            return steps;
        }

        public void setSteps(List<FunnelStep> steps) {
            this.steps = steps;
        }
    }

    public static class FunnelStep{
        private Integer stepId;
        private String name;
        private String color;

        public Integer getStepId() {
            return stepId;
        }

        public void setStepId(Integer stepId) {
            this.stepId = stepId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class DisburseCreditPageConfiguration extends PageDetailConfiguration{
        private Integer approveInteractionContentId;
        private Integer rejectInteractionContentId;

        public Integer getApproveInteractionContentId() {
            return approveInteractionContentId;
        }

        public void setApproveInteractionContentId(Integer approveInteractionContentId) {
            this.approveInteractionContentId = approveInteractionContentId;
        }

        public Integer getRejectInteractionContentId() {
            return rejectInteractionContentId;
        }

        public void setRejectInteractionContentId(Integer rejectInteractionContentId) {
            this.rejectInteractionContentId = rejectInteractionContentId;
        }
    }

    public static class DisbursedCreditPageConfiguration extends PageDetailConfiguration{}

    public static class InProcessCreditPageConfiguration extends PageDetailConfiguration{}

    public static class CallCenterPageConfiguration extends ReportConfiguration{}

    public static class ToVerifyPageConfiguration extends ReportConfiguration{}

    public static class CreateCreditPageConfiguration extends ReportConfiguration{}

    public static class RejectedPageConfiguration extends PageDetailConfiguration{}

    public static class EvaluationPageConfiguration extends PageDetailConfiguration{}

    public static class PaymentCommitmentPageConfiguration extends ReportConfiguration{}

    public static class ReportConfiguration{
        private Integer reportId;

        public Integer getReportId() {
            return reportId;
        }

        public void setReportId(Integer reportId) {
            this.reportId = reportId;
        }
    }

    public static class DetailConfiguration{
        private EditableFieldConfiguration summary;
        private EditableFieldConfiguration applicant;
        private EditableFieldConfiguration incomes;
        private RequestDetailConfiguration request;
        private EditableFieldConfiguration phoneVerification;
        private EditableFieldConfiguration addressVerification;
        private EditableFieldConfiguration welcomeCall;

        public EditableFieldConfiguration getSummary() {
            return summary;
        }

        public void setSummary(EditableFieldConfiguration summary) {
            this.summary = summary;
        }

        public EditableFieldConfiguration getApplicant() {
            return applicant;
        }

        public void setApplicant(EditableFieldConfiguration applicant) {
            this.applicant = applicant;
        }

        public EditableFieldConfiguration getIncomes() {
            return incomes;
        }

        public void setIncomes(EditableFieldConfiguration incomes) {
            this.incomes = incomes;
        }

        public RequestDetailConfiguration getRequest() {
            return request;
        }

        public void setRequest(RequestDetailConfiguration request) {
            this.request = request;
        }

        public EditableFieldConfiguration getPhoneVerification() {
            return phoneVerification;
        }

        public void setPhoneVerification(EditableFieldConfiguration phoneVerification) {
            this.phoneVerification = phoneVerification;
        }

        public EditableFieldConfiguration getAddressVerification() {
            return addressVerification;
        }

        public void setAddressVerification(EditableFieldConfiguration addressVerification) {
            this.addressVerification = addressVerification;
        }

        public EditableFieldConfiguration getWelcomeCall() {
            return welcomeCall;
        }

        public void setWelcomeCall(EditableFieldConfiguration welcomeCall) {
            this.welcomeCall = welcomeCall;
        }
    }

    public static class RequestDetailConfiguration extends EditableFieldConfiguration{
        private EditableFieldConfiguration generalInformation;
        private EditableFieldConfiguration evaluationResult;
        private EditableFieldConfiguration documentation;
        private EditableFieldConfiguration identityValidation;
        private EditableFieldConfiguration fraudAlert;
        private EditableFieldConfiguration bankAccount;
        private EditableFieldConfiguration notes;
        private EditableFieldConfiguration interaction;
        private EditableFieldConfiguration sbs;

        public EditableFieldConfiguration getGeneralInformation() {
            return generalInformation;
        }

        public void setGeneralInformation(EditableFieldConfiguration generalInformation) {
            this.generalInformation = generalInformation;
        }

        public EditableFieldConfiguration getEvaluationResult() {
            return evaluationResult;
        }

        public void setEvaluationResult(EditableFieldConfiguration evaluationResult) {
            this.evaluationResult = evaluationResult;
        }

        public EditableFieldConfiguration getDocumentation() {
            return documentation;
        }

        public void setDocumentation(EditableFieldConfiguration documentation) {
            this.documentation = documentation;
        }

        public EditableFieldConfiguration getIdentityValidation() {
            return identityValidation;
        }

        public void setIdentityValidation(EditableFieldConfiguration identityValidation) {
            this.identityValidation = identityValidation;
        }

        public EditableFieldConfiguration getFraudAlert() {
            return fraudAlert;
        }

        public void setFraudAlert(EditableFieldConfiguration fraudAlert) {
            this.fraudAlert = fraudAlert;
        }

        public EditableFieldConfiguration getBankAccount() {
            return bankAccount;
        }

        public void setBankAccount(EditableFieldConfiguration bankAccount) {
            this.bankAccount = bankAccount;
        }

        public EditableFieldConfiguration getNotes() {
            return notes;
        }

        public void setNotes(EditableFieldConfiguration notes) {
            this.notes = notes;
        }

        public EditableFieldConfiguration getInteraction() {
            return interaction;
        }

        public void setInteraction(EditableFieldConfiguration interaction) {
            this.interaction = interaction;
        }

        public EditableFieldConfiguration getSbs() {
            return sbs;
        }

        public void setSbs(EditableFieldConfiguration sbs) {
            this.sbs = sbs;
        }
    }

    public static class EditableFieldConfiguration{
        private Boolean editable;
        private Boolean visible;

        public Boolean getEditable() {
            return editable;
        }

        public void setEditable(Boolean editable) {
            this.editable = editable;
        }

        public Boolean getVisible() {
            return visible;
        }

        public void setVisible(Boolean visible) {
            this.visible = visible;
        }
    }

    public static class PageDetailConfiguration extends  ReportConfiguration{
        private DetailConfiguration detailConfiguration;

        public DetailConfiguration getDetailConfiguration() {
            return detailConfiguration;
        }

        public void setDetailConfiguration(DetailConfiguration detailConfiguration) {
            this.detailConfiguration = detailConfiguration;
        }
    }


    public String getLoanProgressColor() {
        return loanProgressColor;
    }

    public void setLoanProgressColor(String loanProgressColor) {
        this.loanProgressColor = loanProgressColor;
    }

    public String getOfferActiveColor() {
        return offerActiveColor;
    }

    public void setOfferActiveColor(String offerActiveColor) {
        this.offerActiveColor = offerActiveColor;
    }

    public String getOfferActiveButtonColor() {
        return offerActiveButtonColor;
    }

    public void setOfferActiveButtonColor(String offerActiveButtonColor) {
        this.offerActiveButtonColor = offerActiveButtonColor;
    }

    public String getOfferActiveButtonBackgroundColor() {
        return offerActiveButtonBackgroundColor;
    }

    public void setOfferActiveButtonBackgroundColor(String offerActiveButtonBackgroundColor) {
        this.offerActiveButtonBackgroundColor = offerActiveButtonBackgroundColor;
    }

    public String getSummaryEvaluationBackgroundColor() {
        return summaryEvaluationBackgroundColor;
    }

    public void setSummaryEvaluationBackgroundColor(String summaryEvaluationBackgroundColor) {
        this.summaryEvaluationBackgroundColor = summaryEvaluationBackgroundColor;
    }

    public String getPanelActiveColor() {
        return panelActiveColor;
    }

    public void setPanelActiveColor(String panelActiveColor) {
        this.panelActiveColor = panelActiveColor;
    }

    public String getTabActiveColor() {
        return tabActiveColor;
    }

    public void setTabActiveColor(String tabActiveColor) {
        this.tabActiveColor = tabActiveColor;
    }

    public String getButtonActionBackgroundColor() {
        return buttonActionBackgroundColor;
    }

    public void setButtonActionBackgroundColor(String buttonActionBackgroundColor) {
        this.buttonActionBackgroundColor = buttonActionBackgroundColor;
    }

    public String getButtonActionTextColor() {
        return buttonActionTextColor;
    }

    public void setButtonActionTextColor(String buttonActionTextColor) {
        this.buttonActionTextColor = buttonActionTextColor;
    }

    public String getButtonActionPrincipalBackgroundColor() {
        return buttonActionPrincipalBackgroundColor;
    }

    public void setButtonActionPrincipalBackgroundColor(String buttonActionPrincipalBackgroundColor) {
        this.buttonActionPrincipalBackgroundColor = buttonActionPrincipalBackgroundColor;
    }

    public String getButtonActionPrincipalTextColor() {
        return buttonActionPrincipalTextColor;
    }

    public void setButtonActionPrincipalTextColor(String buttonActionPrincipalTextColor) {
        this.buttonActionPrincipalTextColor = buttonActionPrincipalTextColor;
    }

    public String getButtonActionSecondaryBackgroundColor() {
        return buttonActionSecondaryBackgroundColor;
    }

    public void setButtonActionSecondaryBackgroundColor(String buttonActionSecondaryBackgroundColor) {
        this.buttonActionSecondaryBackgroundColor = buttonActionSecondaryBackgroundColor;
    }

    public String getButtonActionSecondaryTextColor() {
        return buttonActionSecondaryTextColor;
    }

    public void setButtonActionSecondaryTextColor(String buttonActionSecondaryTextColor) {
        this.buttonActionSecondaryTextColor = buttonActionSecondaryTextColor;
    }

    public PaymentCommitmentPageConfiguration getPaymentCommitmentPageConfiguration() {
        return paymentCommitmentPageConfiguration;
    }

    public void setPaymentCommitmentPageConfiguration(PaymentCommitmentPageConfiguration paymentCommitmentPageConfiguration) {
        this.paymentCommitmentPageConfiguration = paymentCommitmentPageConfiguration;
    }
    
    public List<Integer> getProfessionOccupationIds() {
        return professionOccupationIds;
    }

    public void setProfessionOccupationIds(List<Integer> professionOccupationIds) {
        this.professionOccupationIds = professionOccupationIds;
    }

    public List<Integer> getOccupationIds() {
        return occupationIds;
    }

    public void setOccupationIds(List<Integer> occupationIds) {
        this.occupationIds = occupationIds;
    }

}
