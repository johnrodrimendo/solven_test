package com.affirm.common.service.impl;

import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PathService;
import com.affirm.common.util.BellmanFord;
import com.affirm.common.util.Graph;
import com.affirm.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("pathService")
@CacheConfig(cacheNames = "catalogCache", keyGenerator = "cacheKeyGenerator")
public class PathServiceImpl implements PathService {
    @Autowired
    CatalogDAO catalogDAO;

    @Autowired
    CatalogService catalogService;

    @Autowired
    EvaluationService evaluationService;

    @Override
    @Cacheable
    public Graph getGraphByProductCategory(Integer countryId, Integer productCategoryId) throws Exception {
        ProcessQuestionsConfiguration loanConfig = evaluationService.getEvaluationProcessByProductCategory(productCategoryId, countryId, null);

        Graph result = new Graph();
        result.configure(200, true);

        for(ProcessQuestion processQuestion : loanConfig.getQuestions()) {
            if(processQuestion.getResults() == null)
                continue;
            Iterator<String> jsonKeys = processQuestion.getResults().keys();

            if(jsonKeys == null)
                continue;

            while (jsonKeys.hasNext()) {
                Integer nextQuestionId = JsonUtil.getIntFromJson(processQuestion.getResults(), jsonKeys.next(), null);

                if (nextQuestionId != null) {
                    result.addEdge(processQuestion.getId(), nextQuestionId);
                }
            }
        }

        return  result;
    }

    @Override
    public Graph getLargestGraphByProductEntity(Graph prevOffer) throws Exception {
        List<EntityProductParams> entityProductParams = catalogDAO.getEntityProductParams();

        Graph result = null;

        for(EntityProductParams entityProductParam : entityProductParams) {
            Graph aux = getGraphByProductEntity(prevOffer, entityProductParam.getProduct().getId(), entityProductParam.getEntity().getId());

            if(aux == null )
                continue;

            if(result == null || aux.getGraphSize() > result.getGraphSize()) {
                result = aux;
            }
        }

        return result;
    }

    @Override
    @Cacheable
    public Graph getLargestGraphByProductEntity() throws Exception {
        List<EntityProductParams> entityProductParams = catalogDAO.getEntityProductParams();

        Graph result = null;

        for(EntityProductParams entityProductParam : entityProductParams) {
            Graph aux = getGraphByProductEntity(entityProductParam.getProduct().getId(), entityProductParam.getEntity().getId());

            if(aux == null )
                continue;

            if(result == null || aux.getGraphSize() > result.getGraphSize()) {
                result = aux;
            }
        }

        return result;
    }

    @Override
    public Graph getGraphByProductEntity(Graph prev, Integer productId, Integer entityId) throws Exception {
        Graph result = Graph.merge(prev, getGraphByProductEntity(productId, entityId));

        ArrayList<Integer> banned = new ArrayList<>();

        banned.add(ProcessQuestion.Question.Constants.OTHER_INCOME_CAN_DEMONSTRATE);

        BellmanFord bellmanFord = new BellmanFord();
        bellmanFord.setBanned(banned);
        bellmanFord.configure(result);

        result.setGraphSize(bellmanFord.getSize());

        return result;
    }

    @Override
    @Cacheable
    public Graph getGraphByProductEntity(Integer productId, Integer entityId) throws Exception {
        ProcessQuestionsConfiguration entityConfig = catalogService.getEntityProductParam(entityId, productId).getEvaluation();

        Graph result = new Graph();
        result.configure(200, true);

        if(entityConfig == null)
            return result;

        for(ProcessQuestion processQuestion : entityConfig.getQuestions()) {
            if(processQuestion.getResults() == null)
                continue;
            Iterator<String> jsonKeys = processQuestion.getResults().keys();

            if(jsonKeys == null)
                continue;
            while (jsonKeys.hasNext()) {
                Integer nextQuestionId = JsonUtil.getIntFromJson(processQuestion.getResults(), jsonKeys.next(), null);

                if (nextQuestionId != null) {
                    result.addEdge(processQuestion.getId(), nextQuestionId);
                }
            }
        }

        ArrayList<Integer> banned = new ArrayList<>();

        banned.add(ProcessQuestion.Question.Constants.OTHER_INCOME_CAN_DEMONSTRATE);

        BellmanFord bellmanFord = new BellmanFord();
        bellmanFord.setBanned(banned);
        bellmanFord.configure(result);

        result.setGraphSize(bellmanFord.getSize());

        return result;
    }

    public static void main(String[] args ) {
        Graph graph = new Graph();
        graph.configure(18, true);

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(1, 13);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        graph.addEdge(6, 7);
        graph.addEdge(7, 8);
        graph.addEdge(8, 9);
        graph.addEdge(9, 10);
        graph.addEdge(11, 12);
        graph.addEdge(12, 7);
        graph.addEdge(13, 14);
        graph.addEdge(13, 17);
        graph.addEdge(14, 15);
        graph.addEdge(15, 16);
        graph.addEdge(16, 9);
        graph.addEdge(17, 9);

        BellmanFord bellmanFord = new BellmanFord();
        bellmanFord.configure(graph);

        bellmanFord.printDistances();
    }

    @Override
    @org.springframework.context.event.EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
        try {
            System.out.println("DENTRO DE EVENTO DE REFRESCO DE APP");

            getGraphByProductCategory(CountryParam.COUNTRY_PERU, ProductCategory.CONSUMO);
            getGraphByProductCategory(CountryParam.COUNTRY_PERU, ProductCategory.VEHICULO);
            getGraphByProductCategory(CountryParam.COUNTRY_PERU, ProductCategory.CONSOLIDAR_CREDITOS);
            getLargestGraphByProductEntity();

        } catch (Throwable th) {
            ErrorServiceImpl.onErrorStatic(th);
        }
    }
}
