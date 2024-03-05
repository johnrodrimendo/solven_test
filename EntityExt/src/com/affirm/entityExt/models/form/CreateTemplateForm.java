package com.affirm.entityExt.models.form;

import com.affirm.common.util.*;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import org.cactoos.scalar.False;

import java.io.Serializable;

public class CreateTemplateForm extends FormGeneric implements Serializable {

    private String headerImg;
    private String body;
    private String subject;
    private String name;
    private Integer parentTemplateId;
    private Character type;


    public CreateTemplateForm() {
        this.setValidator( new CreateTemplateFormValidator());
    }

    public class CreateTemplateFormValidator extends FormValidator implements Serializable {

        private StringFieldValidator headerImg;
        private StringFieldValidator body;
        private StringFieldValidator subject;
        private StringFieldValidator name;
        private IntegerFieldValidator parentTemplateId;
        private CharFieldValidator type;

        public CreateTemplateFormValidator() {
            addValidator(type = new CharFieldValidator().setRequired(true));
            addValidator(body = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(headerImg = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(subject = new StringFieldValidator().setValidRegex(null).setMaxCharacters(null).setRequired(true));
            addValidator(name = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(parentTemplateId = new IntegerFieldValidator().setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {

            if(type.getValue() != null && type.getValue().equals(MarketingCampaign.EMAIL)) {
                headerImg.setRequired(true);
                subject.setRequired(true);
            } else if(type.getValue() != null && type.getValue().equals(MarketingCampaign.SMS)) {
                headerImg.setRequired(false);
                subject.setRequired(false);
                body.setMaxCharacters(140);
            }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return CreateTemplateForm.this;
        }
    }


    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String urlImage) {
        this.headerImg = urlImage;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentTemplateId() {
        return parentTemplateId;
    }

    public void setParentTemplateId(Integer template_id) {
        this.parentTemplateId = template_id;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }
}
