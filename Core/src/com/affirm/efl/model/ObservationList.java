package com.affirm.efl.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.AccountConsolidableDebt;
import com.affirm.common.model.transactional.EntityConsolidableDebt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 04/07/17.
 */
public class ObservationList {

    private List<String> selectedValues;
    private List<String> selectedLabels;

    public ObservationList(int entityId, TranslatorDAO translatorDAO, List<EntityConsolidableDebt> products) throws Exception{
        this.selectedValues = new ArrayList<>();
        this.selectedLabels = new ArrayList<>();
        if(products != null){
            for(EntityConsolidableDebt product : products){
                if(product.getAccounts() != null && product.getAccounts().size() > 0){
                    for(AccountConsolidableDebt subProduct : product.getAccounts()){
                        String value = translatorDAO.translate(entityId, 19, String.valueOf(subProduct.getAccount().getId()), null);
                        if(value != null && !isDuplicated(value)){
                            selectedValues.add(value);
                            if(subProduct.getAccount() != null && subProduct.getAccount().getName() != null)
                                selectedLabels.add(subProduct.getAccount().getName());
                        }
                    }
                }
            }
        }
    }

    private boolean isDuplicated(String value){
        if(selectedValues != null && selectedValues.size() > 0){
            for(String selectedValue : selectedValues){
                if(selectedValue.equals(value)) return true;
            }
        }else{
            return false;
        }
        return false;
    }

    public List<String> getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(List<String> selectedValues) {
        this.selectedValues = selectedValues;
    }

    public List<String> getSelectedLabels() {
        return selectedLabels;
    }

    public void setSelectedLabels(List<String> selectedLabels) {
        this.selectedLabels = selectedLabels;
    }
}
