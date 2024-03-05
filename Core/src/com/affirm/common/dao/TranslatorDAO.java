package com.affirm.common.dao;

import org.json.JSONObject;

/**
 * Created by dev5 on 12/04/17.
 */
public interface TranslatorDAO {

    String translate(Integer entityId, Integer tableId, String valorSolven, String valorEntidad) throws Exception;

    JSONObject translateLocality(Integer entityId, Long localityId);

}
