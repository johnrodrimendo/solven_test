package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by renzodiaz on 4/26/17.
 */
public class EntityBrandingLoanLandingConfiguration implements Serializable {
    public static final Integer QUESTION_TO_GO = 124;
    public static final String BACKGROUND_IMAGE_URL = "https://s3.amazonaws.com/solven-public/img/solven-credito-consumo.jpg";
    public static final String IMAGE_MESSAGE = "static.landing5.imageMessage";
    public static final String IMAGE_SUB_MESSAGE = "static.landing5.imageSubMessage";
    public static final String TAGLINE_1 = "static.landing5.tagline.1";
    public static final String TAGLINE_2 = "static.landing5.tagline.2";
    public static final String TAGLINE_3 = "static.landing5.tagline.3";
    public static final String TAGLINE_4 = "static.landing5.tagline.4";
    public static final String FORM_HEAD_LINE = "static.landing5.formHeadLine";

    private Integer questionToGo;
    private String backgroundImageUrl;
    private String imageMessage;
    private String imageSubMessage;
    private String tagline1;
    private String tagline2;
    private String tagline3;
    private String tagline4;
    private String formHeadLine;
    private String logoImageUrl;
    EntityBrandingRequirement entityBrandingRequirement;
    private String mobileHeaderBackgroundColor;
    private String mobileHeaderTextColor;
    private List<Integer> loanReasons = new ArrayList<>();
    private String fontFamily;
    private Boolean hideTitlePoints;
    private Boolean showTagLineAsColumns;
    private Boolean hidePoweredBy;
    private Boolean hideTagsLines;
    private List<String> hideElements = new ArrayList<>();
    private String formButtonText;
    private String customColorForImageMessage;
    private String customColorForImageSubMessage;
    private String customColorForFormButton;
    private String customColorForHeadlineText;
    private String customColorForHeadlineUnderline;
    private String mobileLogoUrl;
    private List<String> additionalElements = new ArrayList<>();
    private List<Integer> authenticationRequiredByCategory = new ArrayList<>();

    public EntityBrandingLoanLandingConfiguration() {
        entityBrandingRequirement = new EntityBrandingRequirement();
    }

    public void fillFromDb(JSONObject json) throws Exception {
        setQuestionToGo(JsonUtil.getIntFromJson(json, "questionToGo", null));

        String biu = JsonUtil.getStringFromJson(json, "backgroundImageUrl", null);
        setBackgroundImageUrl(biu != null ? biu : BACKGROUND_IMAGE_URL);

        setImageMessage(JsonUtil.getStringFromJson(json, "imageMessage", null));
        setImageSubMessage(JsonUtil.getStringFromJson(json, "imageSubMessage", null));
        setTagline1(JsonUtil.getStringFromJson(json, "tagline1", null));
        setTagline2(JsonUtil.getStringFromJson(json, "tagline2", null));
        setTagline3(JsonUtil.getStringFromJson(json, "tagline3", null));
        setTagline4(JsonUtil.getStringFromJson(json, "tagline4", null));
        setFormHeadLine(JsonUtil.getStringFromJson(json, "formHeadLine", null));

        EntityBrandingRequirement entityBrandingRequirement = new EntityBrandingRequirement();

        if (json != null && JsonUtil.getJsonObjectFromJson(json, "entityBrandingRequirement", null) != null) {
            JSONObject reqJson = JsonUtil.getJsonObjectFromJson(json, "entityBrandingRequirement", null);

            entityBrandingRequirement.setTitle(JsonUtil.getStringFromJson(reqJson, "title", null));
            entityBrandingRequirement.setBoldText(JsonUtil.getStringFromJson(reqJson, "boldText", null));
            JSONArray requirementsArray = JsonUtil.getJsonArrayFromJson(reqJson, "list", null);
            for (Object jsonObject : requirementsArray) {
                entityBrandingRequirement.getList().add(jsonObject.toString());
            }
        }
        setEntityBrandingRequirement(entityBrandingRequirement);
        setLogoImageUrl(JsonUtil.getStringFromJson(json, "logoImageUrl", null));
        setMobileHeaderBackgroundColor(JsonUtil.getStringFromJson(json, "mobileHeaderBackgroundColor", null));
        setMobileHeaderTextColor(JsonUtil.getStringFromJson(json, "mobileHeaderTextColor", null));

        if (json != null && JsonUtil.getJsonArrayFromJson(json, "loanReasons", null) != null) {
            JSONArray reasons = JsonUtil.getJsonArrayFromJson(json, "loanReasons", null);
            for (int i = 0; i < reasons.length(); i++) {
                loanReasons.add(reasons.getInt(i));
            }
        }

        setFontFamily(JsonUtil.getStringFromJson(json, "fontFamily", null));
        setHideTitlePoints(JsonUtil.getBooleanFromJson(json, "hideTitlePoints", false));
        setShowTagLineAsColumns(JsonUtil.getBooleanFromJson(json, "showTagLineAsColumns", false));
        setHidePoweredBy(JsonUtil.getBooleanFromJson(json, "hidePoweredBy", false));
        setHideTagsLines(JsonUtil.getBooleanFromJson(json, "hideTagsLines", false));

        if (json != null && JsonUtil.getJsonArrayFromJson(json, "hideElements", null) != null) {
            JSONArray reasons = JsonUtil.getJsonArrayFromJson(json, "hideElements", null);
            for (int i = 0; i < reasons.length(); i++) {
                hideElements.add(reasons.getString(i));
            }
        }
        setFormButtonText(JsonUtil.getStringFromJson(json, "formButtonText", null));
        setCustomColorForFormButton(JsonUtil.getStringFromJson(json, "customColorForFormButton", null));
        setCustomColorForImageSubMessage(JsonUtil.getStringFromJson(json, "customColorForImageSubMessage", null));
        setCustomColorForImageMessage(JsonUtil.getStringFromJson(json, "customColorForImageMessage", null));
        setCustomColorForHeadlineText(JsonUtil.getStringFromJson(json, "customColorForHeadlineText", null));
        setCustomColorForHeadlineUnderline(JsonUtil.getStringFromJson(json, "customColorForHeadlineUnderline", null));
        setMobileLogoUrl(JsonUtil.getStringFromJson(json, "mobileLogoUrl", null));

        if (json != null && JsonUtil.getJsonArrayFromJson(json, "showAdditionalElements", null) != null) {
            JSONArray additionalElementsData = JsonUtil.getJsonArrayFromJson(json, "showAdditionalElements", null);
            for (int i = 0; i < additionalElementsData.length(); i++) {
                additionalElements.add(additionalElementsData.getString(i));
            }
        }

        if (json != null && JsonUtil.getJsonArrayFromJson(json, "authenticationRequiredByCategory", null) != null) {
            JSONArray authenticationRequired = JsonUtil.getJsonArrayFromJson(json, "authenticationRequiredByCategory", null);
            if(authenticationRequiredByCategory == null) authenticationRequiredByCategory = new ArrayList<>();
            for (int i = 0; i < authenticationRequired.length(); i++) {
                authenticationRequiredByCategory.add(authenticationRequired.getInt(i));
            }
        }

    }


    public void fillDefaultValues(MessageSource messageSource, Locale locale) {
        if (getBackgroundImageUrl() == null)
            setBackgroundImageUrl(BACKGROUND_IMAGE_URL);
        if (getImageMessage() == null)
            setImageMessage(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.IMAGE_MESSAGE, null, locale));
        if (getImageSubMessage() == null)
            setImageSubMessage(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.IMAGE_SUB_MESSAGE, null, locale));
        if (getTagline1() == null)
            setTagline1(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.TAGLINE_1, null, locale));
        if (getTagline2() == null)
            setTagline2(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.TAGLINE_2, null, locale));
        if (getTagline3() == null)
            setTagline3(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.TAGLINE_3, null, locale));
        if (getTagline4() == null)
            setTagline4(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.TAGLINE_4, null, locale));
        if (getFormHeadLine() == null)
            setFormHeadLine(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.FORM_HEAD_LINE, null, locale));
        if (getEntityBrandingRequirement().getTitle() == null)
            getEntityBrandingRequirement().setTitle(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.TITLE, null, locale));
        if (getEntityBrandingRequirement().getBoldText() == null)
            getEntityBrandingRequirement().setBoldText(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.BOLD_TEXT, null, locale));
        if (getEntityBrandingRequirement().getList().isEmpty()) {
            getEntityBrandingRequirement().getList().add(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.LIST_1, null, locale));
            getEntityBrandingRequirement().getList().add(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.LIST_2, null, locale));
            getEntityBrandingRequirement().getList().add(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.LIST_3, null, locale));
            getEntityBrandingRequirement().getList().add(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.LIST_4, null, locale));
            getEntityBrandingRequirement().getList().add(messageSource.getMessage(EntityBrandingLoanLandingConfiguration.EntityBrandingRequirement.LIST_5, null, locale));
        }
    }

    public Integer getQuestionToGo() {
        return questionToGo;
    }

    public void setQuestionToGo(Integer questionToGo) {
        this.questionToGo = questionToGo;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getImageMessage() {
        return imageMessage;
    }

    public void setImageMessage(String imageMessage) {
        this.imageMessage = imageMessage;
    }

    public String getTagline1() {
        return tagline1;
    }

    public void setTagline1(String tagline1) {
        this.tagline1 = tagline1;
    }

    public String getTagline2() {
        return tagline2;
    }

    public void setTagline2(String tagline2) {
        this.tagline2 = tagline2;
    }

    public String getTagline3() {
        return tagline3;
    }

    public void setTagline3(String tagline3) {
        this.tagline3 = tagline3;
    }

    public String getTagline4() {
        return tagline4;
    }

    public void setTagline4(String tagline4) {
        this.tagline4 = tagline4;
    }

    public String getFormHeadLine() {
        return formHeadLine;
    }

    public void setFormHeadLine(String formHeadLine) {
        this.formHeadLine = formHeadLine;
    }

    public EntityBrandingRequirement getEntityBrandingRequirement() {
        return entityBrandingRequirement;
    }

    public void setEntityBrandingRequirement(EntityBrandingRequirement entityBrandingRequirement) {
        this.entityBrandingRequirement = entityBrandingRequirement;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }

    public String getMobileHeaderBackgroundColor() {
        return mobileHeaderBackgroundColor;
    }

    public void setMobileHeaderBackgroundColor(String mobileHeaderBackgroundColor) {
        this.mobileHeaderBackgroundColor = mobileHeaderBackgroundColor;
    }

    public String getMobileHeaderTextColor() {
        return mobileHeaderTextColor;
    }

    public void setMobileHeaderTextColor(String mobileHeaderTextColor) {
        this.mobileHeaderTextColor = mobileHeaderTextColor;
    }

    public List<Integer> getLoanReasons() {
        return loanReasons;
    }

    public void setLoanReasons(List<Integer> loanReasons) {
        this.loanReasons = loanReasons;
    }

    public String getImageSubMessage() {
        return imageSubMessage;
    }

    public void setImageSubMessage(String imageSubMessage) {
        this.imageSubMessage = imageSubMessage;
    }

    public class EntityBrandingRequirement {

        public final static String TITLE = "static.landing5.requirements.title";
        public final static String BOLD_TEXT = "static.landing5.requirements.boldText";
        public final static String LIST_1 = "static.landing5.requirements.list.1";
        public final static String LIST_2 = "static.landing5.requirements.list.2";
        public final static String LIST_3 = "static.landing5.requirements.list.3";
        public final static String LIST_4 = "static.landing5.requirements.list.4";
        public final static String LIST_5 = "static.landing5.requirements.list.5";

        String title;
        String boldText;
        List<String> list;

        EntityBrandingRequirement() {
            list = new ArrayList<>();
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBoldText() {
            return boldText;
        }

        public void setBoldText(String boldText) {
            this.boldText = boldText;
        }


    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Boolean getHideTitlePoints() {
        return hideTitlePoints;
    }

    public void setHideTitlePoints(Boolean hideTitlePoints) {
        this.hideTitlePoints = hideTitlePoints;
    }

    public Boolean getShowTagLineAsColumns() {
        return showTagLineAsColumns;
    }

    public void setShowTagLineAsColumns(Boolean showTagLineAsColumns) {
        this.showTagLineAsColumns = showTagLineAsColumns;
    }

    public Boolean getHidePoweredBy() {
        return hidePoweredBy;
    }

    public void setHidePoweredBy(Boolean hidePoweredBy) {
        this.hidePoweredBy = hidePoweredBy;
    }

    public Boolean getHideTagsLines() {
        return hideTagsLines;
    }

    public void setHideTagsLines(Boolean hideTagsLines) {
        this.hideTagsLines = hideTagsLines;
    }

    public List<String> getHideElements() {
        return hideElements;
    }

    public void setHideElements(List<String> hideElements) {
        this.hideElements = hideElements;
    }

    public String getFormButtonText() {
        return formButtonText;
    }

    public void setFormButtonText(String formButtonText) {
        this.formButtonText = formButtonText;
    }

    public String getCustomColorForImageMessage() {
        return customColorForImageMessage;
    }

    public void setCustomColorForImageMessage(String customColorForImageMessage) {
        this.customColorForImageMessage = customColorForImageMessage;
    }

    public String getCustomColorForImageSubMessage() {
        return customColorForImageSubMessage;
    }

    public void setCustomColorForImageSubMessage(String customColorForImageSubMessage) {
        this.customColorForImageSubMessage = customColorForImageSubMessage;
    }

    public String getCustomColorForFormButton() {
        return customColorForFormButton;
    }

    public void setCustomColorForFormButton(String customColorForFormButton) {
        this.customColorForFormButton = customColorForFormButton;
    }

    public String getCustomColorForHeadlineText() {
        return customColorForHeadlineText;
    }

    public void setCustomColorForHeadlineText(String customColorForHeadlineText) {
        this.customColorForHeadlineText = customColorForHeadlineText;
    }

    public String getCustomColorForHeadlineUnderline() {
        return customColorForHeadlineUnderline;
    }

    public void setCustomColorForHeadlineUnderline(String customColorForHeadlineUnderline) {
        this.customColorForHeadlineUnderline = customColorForHeadlineUnderline;
    }

    public String getMobileLogoUrl() {
        return mobileLogoUrl;
    }

    public void setMobileLogoUrl(String mobileLogoUrl) {
        this.mobileLogoUrl = mobileLogoUrl;
    }

    public List<String> getAdditionalElements() {
        return additionalElements;
    }

    public void setAdditionalElements(List<String> additionalElements) {
        this.additionalElements = additionalElements;
    }

    public List<Integer> getAuthenticationRequiredByCategory() {
        return authenticationRequiredByCategory;
    }

    public void setAuthenticationRequiredByCategory(List<Integer> authenticationRequiredByCategory) {
        this.authenticationRequiredByCategory = authenticationRequiredByCategory;
    }
}
