package com.affirm.common.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;

import java.util.List;

public class SenderMailConfiguration {

    private List<Integer> interactionContentId;
    private String name;
    private String email;

    public List<Integer> getInteractionContentId() {
        return interactionContentId;
    }

    public void setInteractionContentId(List<Integer> interactionContentId) {
        this.interactionContentId = interactionContentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static SenderMailConfiguration getSenderMailConfiguration(CatalogService catalogService, LoanApplication loanApplication,Integer interactionContentId){
        if(loanApplication == null) return null;
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        if(entityId == null) return null;
        Entity entity = catalogService.getEntity(entityId);
        List<SenderMailConfiguration> senderMailConfigurations = entity.getSenderMailConfiguration();
        if(senderMailConfigurations == null || senderMailConfigurations.isEmpty()) return null;
        for (SenderMailConfiguration senderMailConfiguration : senderMailConfigurations) {
            if(senderMailConfiguration.getInteractionContentId() != null && senderMailConfiguration.getInteractionContentId().contains(interactionContentId)) return senderMailConfiguration;
        }
        return null;
    }

}
