package com.affirm.strategy;

import com.affirm.common.model.transactional.LoanOffer;
import org.json.JSONObject;

import java.util.List;

public class MatchTransactionalParams {


    interface MatchTransParam {
        LoanOffer processParams(List<LoanOffer> rejectedOffers, JSONObject jsonVars);
    }

    class MatchTransFindLowestRate implements MatchTransParam {
        //teaTooHigh
        @Override
        public LoanOffer processParams(List<LoanOffer> rejectedOffers, JSONObject jsonVars) {
            int maxValueIndex = 0;


            for (int i = 1; i < rejectedOffers.size(); i++) {
                if (rejectedOffers.get(i).getEffectiveAnualRate() < rejectedOffers.get(maxValueIndex).getEffectiveAnualRate()) {
                    maxValueIndex = i;
                }
            }
            return rejectedOffers.get(maxValueIndex);

        }
    }

    class MatchTransParamDefaultFindHeighestAmount implements MatchTransParam {
        //other
        @Override
        public LoanOffer processParams(List<LoanOffer> rejectedOffers, JSONObject jsonVars) {
            return highestAmountOffer(rejectedOffers, jsonVars);
        }
    }

    private LoanOffer highestAmountOffer(List<LoanOffer> rejectedOffers, JSONObject jsonVars) {
        int maxValueIndex = 0;

        for (int i = 1; i < rejectedOffers.size(); i++) {
            if (rejectedOffers.get(i).getAmmount() > rejectedOffers.get(maxValueIndex).getAmmount()) {
                maxValueIndex = i;
            }
        }
        return rejectedOffers.get(maxValueIndex);
    }
}
