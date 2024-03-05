package com.affirm.client.model.wordpress;

import com.affirm.system.configuration.Configuration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by renzodiaz on 6/20/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralWordPressPostObject {

    @JsonProperty("rendered")
    private String rendered;
    @JsonProperty("protected")
    private Boolean isProtected;

    public String getRendered() {

        String result = rendered.replaceAll("https://docs.solven.pe", Configuration.getClientDomain()+"/blog");

        return result;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

    public Boolean getProtected() {
        return isProtected;
    }

    public void setProtected(Boolean aProtected) {
        isProtected = aProtected;
    }
}
