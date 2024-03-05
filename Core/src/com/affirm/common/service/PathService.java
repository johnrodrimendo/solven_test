package com.affirm.common.service;

import com.affirm.common.util.Graph;
import org.springframework.context.event.ContextRefreshedEvent;

public interface PathService {

    Graph getLargestGraphByProductEntity(Graph prevOffer) throws Exception;
    Graph getLargestGraphByProductEntity() throws Exception;
    Graph getGraphByProductEntity(Graph prevOffer, Integer productId, Integer entityId) throws Exception;
    Graph getGraphByProductEntity(Integer productId, Integer entityId) throws Exception;
    Graph getGraphByProductCategory(Integer countryId, Integer productCategoryId) throws Exception;

    @org.springframework.context.event.EventListener
    void handleContextRefresh(ContextRefreshedEvent event) throws Exception;
}
