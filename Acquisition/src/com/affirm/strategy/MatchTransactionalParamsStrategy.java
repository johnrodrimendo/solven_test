package com.affirm.strategy;

import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.transactional.LoanOffer;
import org.json.JSONObject;

import java.util.List;

public class MatchTransactionalParamsStrategy {



    public LoanOffer resolveParameters(Integer interactionContentId, List<LoanOffer> rejectedOffers, JSONObject json) {

        MatchTransactionalParams matchTransParams = new MatchTransactionalParams();

        switch (interactionContentId) {
            case InteractionContent.RECHAZO_EN_OFERTA_3:
                return matchTransParams.new MatchTransFindLowestRate().processParams(rejectedOffers, json);
            case InteractionContent.RECHAZO_EN_OFERTA_1:
            case InteractionContent.RECHAZO_EN_OFERTA_4:
            case InteractionContent.RECHAZO_EN_OFERTA_6:
            case InteractionContent.RECHAZO_EN_OFERTA_7:
            case InteractionContent.RECHAZO_EN_OFERTA_2:
                return matchTransParams.new MatchTransParamDefaultFindHeighestAmount().processParams(rejectedOffers, json);
            default:
                return null;
        }
    }
}
