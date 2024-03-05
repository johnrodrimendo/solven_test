package com.affirm.common.dao;

import com.affirm.common.model.transactional.Conversion;

import java.util.List;

public interface ConversionDAO {

    void registerPixelConversion(Integer loanApplicationId, String pixelEntity, String instance);

    List<Conversion> getConversions(Integer loanApplicationId);
}
