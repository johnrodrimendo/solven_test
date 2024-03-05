package com.affirm.backoffice.service.impl;

import org.springframework.stereotype.Service;

/**
 * Created by jrodriguez on 15/07/16.
 */
@Service("frontEndService")
public class FrontEndServiceImpl {

    private String defaultPortletClass = "portlet light";
    private String defaultCaptionClass = "caption font-red-sharp";
    private String defaultCaptionSubjectClass = "caption-subject font-green-steel bold";
    private String defaultActionButtonClass = "btn btn-circle btn-default";
    private String defaultDropdownActionButtonClass = "btn green-haze btn-outline btn-circle";
    private String defaultInsidePortletButtonClass = "btn green btn-outline btn-xs";
    private String defaultInsidePortletRejectButtonClass = "btn red btn-outline btn-xs";

    public String getDefaultPortletClass() {
        return defaultPortletClass;
    }

    public void setDefaultPortletClass(String defaultPortletClass) {
        this.defaultPortletClass = defaultPortletClass;
    }

    public String getDefaultCaptionSubjectClass() {
        return defaultCaptionSubjectClass;
    }

    public void setDefaultCaptionSubjectClass(String defaultCaptionSubjectClass) {
        this.defaultCaptionSubjectClass = defaultCaptionSubjectClass;
    }

    public String getDefaultCaptionClass() {
        return defaultCaptionClass;
    }

    public void setDefaultCaptionClass(String defaultCaptionClass) {
        this.defaultCaptionClass = defaultCaptionClass;
    }

    public String getDefaultActionButtonClass() {
        return defaultActionButtonClass;
    }

    public void setDefaultActionButtonClass(String defaultActionButtonClass) {
        this.defaultActionButtonClass = defaultActionButtonClass;
    }

    public String getDefaultDropdownActionButtonClass() {
        return defaultDropdownActionButtonClass;
    }

    public void setDefaultDropdownActionButtonClass(String defaultDropdownActionButtonClass) {
        this.defaultDropdownActionButtonClass = defaultDropdownActionButtonClass;
    }

    public String getDefaultInsidePortletButtonClass() {
        return defaultInsidePortletButtonClass;
    }

    public void setDefaultInsidePortletButtonClass(String defaultInsidePortletButtonClass) {
        this.defaultInsidePortletButtonClass = defaultInsidePortletButtonClass;
    }

    public String getDefaultInsidePortletRejectButtonClass() {
        return defaultInsidePortletRejectButtonClass;
    }

    public void setDefaultInsidePortletRejectButtonClass(String defaultInsidePortletRejectButtonClass) {
        this.defaultInsidePortletRejectButtonClass = defaultInsidePortletRejectButtonClass;
    }
}
