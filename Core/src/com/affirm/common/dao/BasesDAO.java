package com.affirm.common.dao;

import com.affirm.common.model.transactional.AztecaGatewayBasePhone;
import com.affirm.common.model.transactional.GatewayBaseEvent;

import java.util.Date;
import java.util.List;

public interface BasesDAO {
    List<AztecaGatewayBasePhone> getAztecaCobranzaPhones(int offset, int limit);

    Integer getAztecaCobranzaPhonesCount();

    GatewayBaseEvent registerCollectionBaseEvent(Integer entityId, Date registerDate, Character type, Character status);

    void updateCollectionBaseEventStatus(int id, Character status) throws Exception;

    void updateCollectionBaseEventFinishDate(int id, Date finishDate) throws Exception;

    void updateCollectionBaseEventCounts(int id, Integer success, Integer failed) throws Exception;

    GatewayBaseEvent getLastCollectionBaseEventByType(Integer entityId, Character type);
}
