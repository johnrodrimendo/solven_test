package com.affirm.common.dao;

import com.affirm.common.model.transactional.BaseCount;
import com.affirm.common.model.transactional.GatewayBaseEvent;
import com.affirm.common.model.transactional.EntityGatewayBaseDetail;
import com.affirm.common.model.transactional.EntityExtranetUser;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface EntityExtranetDAO {

    void registerLogActivity(String permission, int entityExtranetUserId);

    void registerEntityUserRole(int entiyUserId, List<Integer> roles);

    void saveBatchCommissionCluster(Integer entityId, Integer productId, Integer priceId, Integer clusterId, String clustersJson) throws Exception;

    List<GatewayBaseEvent> getCollectionBaseEvent(Integer entityId, Date startDate, Date endDate, Integer offset, Integer limit, Locale locale)  throws Exception;

    Pair<Integer, Double> getCollectionBaseEventCount(Integer entityId, Date startDate, Date endDate, Locale locale) throws Exception;

    BaseCount getBaseCount(Integer entityId, Character type, Locale locale) throws Exception;

    EntityGatewayBaseDetail getEntityCOllectionBaseDetail(Integer entityId) throws Exception;

    void updateEntityUserInformation(int entityUserId, Integer entityId, List<EntityExtranetUser.MenuEntityProductCategory> entityProductCategories);
}
