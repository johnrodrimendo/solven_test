package com.affirm.backoffice.strategies.bureau;

import com.affirm.common.model.catalog.Bureau;

public class BureauContext {
    private BureauStrategy strategy;

    public void setStrategy(int bureauId) {
        switch (bureauId) {
            case Bureau.EQUIFAX:
            case Bureau.EQUIFAX_RUC:
                setStrategy(new EquifaxBOStrategy());
                break;
            case Bureau.NOSIS:
                setStrategy(new NosisBOStrategy());
                break;
        }
    }

    public BureauStrategy getStrategy() { return strategy; }

    public void setStrategy(BureauStrategy strategy) { this.strategy = strategy; }
}
