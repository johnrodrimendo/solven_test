/**
 *
 */
package com.affirm.common.dao;

import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.common.model.transactional.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */
public interface RccDAO {
    List<RccSynthesized> getRccSynthesized(String documentNumber) throws Exception;

    List<RccDate> getRccDates() throws Exception;

    RccScore getRccScore(String documentNumber) throws Exception;

    List<RccIdeGrouped> getRccIdeGrouped(String documentNumber) throws Exception;

    List<CendeuDate> getCendeuDates() throws Exception;

    Boolean getCendeuRejectedChecks(String documentNumber) throws Exception;

    List<BDSBase> getBancoDelSolBase(String documentNumber) throws Exception;

    Integer getMaxScoreMonthly(String ruc, Integer start, Integer end);

    List<RccSynthesizedExtraFields> getRccSynthesizedExtraFields(String documentNumber) throws Exception;

    BDSBaseProcess getCurrentBancoDelSolBaseProcess() ;

    Boolean existsInFinansolPreApprovedBase(String documentNumber) throws Exception;

    FinansolPreApprovedBase getFinansolPreApprovedBase(String documentType, String documentNumber);

    CampaniaBds getLastCampaniaBds(String documentNumber);

    CampaniaBds getCampaniaBdsById(Integer id);

    long copyCSVToPrismaTable(File file, String table) throws IOException, SQLException;

    long copyCSVToTable(File file, String table, Boolean truncate) throws IOException, SQLException;

    long copyCSVToTable(File file, String table, Boolean truncate, String additionalCommand) throws IOException, SQLException;

    void copyTableToAnother(String tableFrom, String tableTo);

    void truncateTable(String table);
    
//    Boolean existsInBanBifPreApprovedBase(String documentType, String documentNumber) throws Exception;

    List<BanbifPreApprovedBase> getBanbifPreApprovedBase(String documentType, String documentNumber);

    Boolean existsInPrismaPreApprovedBase(String documentNumber) throws Exception;

    Boolean existsInAztecaPreApprovedBase(String documentNumber) throws Exception;

    AztecaPreApprovedBase getAztecaPreApprovedBase(String documentNumber);
    
    PrismaPreApprovedBase getPrismaPreApprovedBase(String documentType, String documentNumber);

    Boolean existsInBDSCNEBase(String documentNumber) throws Exception;

    List<CendeuRejectedCheck> getCendeuRejectedChecksByCuit(String documentNumber) throws Exception;

    AztecaGetawayBase getAztecaCobranzaBase(String documentType, String documentNumber);

    List<AztecaGetawayBase> getMaretingCampaignDestinations(Integer entityId, Character filterType);

    List<AztecaGetawayBase> getAztecaCobranzaBases(String documentType, String documentNumber);

    Date getBanbifBaseValidUntil();
}