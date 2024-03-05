package com.affirm.common.service;

public interface QuestionPercentageService {
    double getCurrentCategoryPercentage(Integer questionId, Integer countryId, Integer productCategoryId, double previousPercentage, Integer loanApplicationId) throws Exception;

    public double getCurrentPercentage(Integer questionId, Integer countryId, Integer categoryId, Integer entityId, Integer productId, double previousPercentage) throws Exception;
}
