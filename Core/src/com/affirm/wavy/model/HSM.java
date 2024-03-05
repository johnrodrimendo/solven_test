package com.affirm.wavy.model;

import java.util.List;

public class HSM {

    public enum LanguagePolicy {
        FALLBACK,
        DETERMINISTIC
    }

    private String namespace;
    private String elementName;
    private List<String> parameters;
    private LanguagePolicy languagePolicy;
    private String languageCode;

    public HSM() {
        this.namespace = "whatsapp:hsm:ecommerce:movile";
        this.languagePolicy = LanguagePolicy.FALLBACK;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public LanguagePolicy getLanguagePolicy() {
        return languagePolicy;
    }

    public void setLanguagePolicy(LanguagePolicy languagePolicy) {
        this.languagePolicy = languagePolicy;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
