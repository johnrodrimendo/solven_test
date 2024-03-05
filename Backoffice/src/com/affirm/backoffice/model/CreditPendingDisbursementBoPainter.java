package com.affirm.backoffice.model;

import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by sTbn on 15/08/16.
 */
public class CreditPendingDisbursementBoPainter extends Credit implements IPaginationWrapperElement {

    private EntityProductParams entityProductParams;
    private Boolean entityValidated;

    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        if (getProduct() != null && getEntity() != null)
            setEntityProductParams(catalog.getEntityProductParamById(getEntityProductParameterId()));
        setEntityValidated(JsonUtil.getBooleanFromJson(json, "person_entity_validated", null));
    }

    public EntityProductParams getEntityProductParams() {
        return entityProductParams;
    }

    public void setEntityProductParams(EntityProductParams entityProductParams) {
        this.entityProductParams = entityProductParams;
    }

    public Boolean getEntityValidated() {
        return entityValidated;
    }

    public void setEntityValidated(Boolean entityValidated) {
        this.entityValidated = entityValidated;
    }
}
