package com.affirm.backoffice.dao;

import com.affirm.backoffice.model.LineaResultBoPainter;
import com.affirm.common.model.transactional.*;

import java.util.List;

/**
 * @author jrodriguez
 */
public interface PersonBODAO {

    ReniecResult getReniecResult(int personId) throws Exception;

    EssaludResult getEssaludResult(int personId) throws Exception;

    List<LineaResultBoPainter> getPhoneContractLineaResult(int personId) throws Exception;

    SatResult getSatResult(int personId) throws Exception;

    SisResult getSisResult(int personId) throws Exception;

    List<SoatRecordsResult> getSoatRecordsResults(int personId) throws Exception;


}
