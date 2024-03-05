package com.affirm.security.dao;

import com.affirm.common.model.RekognitionProData;
import com.affirm.common.model.RekognitionReniecData;
import com.affirm.common.model.transactional.*;
import com.affirm.heroku.model.ServerStatus;
import com.affirm.security.model.EntityWsResult;
import com.affirm.security.model.SysUser;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by jrodriguez on 19/09/16.
 */
public interface SecurityDAO {
    SysUser getSysUser(String userName);

    void registerException(String stackTrace) throws Exception;

    void registerExternalAttack(String attackType, String attackerIP, Double ipLatitude, Double ipLongitude, String detail) throws Exception;

    EntityWsResult getEntityResultWS(Integer loanApplicationId, Integer entityWS) throws Exception;

    Integer getPermissionId(String permission);

    List<ExtranetMenuEntity> getExtranetMenuEntities(int entityId);

    void registerServerStatus(ServerStatus serverStatus);

    List<ServerStatus> getServerStatus(Date from);

    MatiResult registerMatiResult(Integer loanApplicationId, Integer queryId);

    List<MatiResult> getMatiResultsByLoanApplication(int loanApplicationId);

    void updateMatiResult(int matiResultId, Date finishDate, String response, Integer status) throws Exception;

    void updateMatiResultLoanApplicationId(int matiResultId, Integer loanApplicationId) throws Exception;

    void updateMatiResultVerificationId(int matiResultId, String verificationId) throws Exception;

    void updateMatiStatus(int matiResultId, Integer status) throws Exception;

    RekognitionProProcess getRekognitionProProcess(int loanApplicationId, char type, Integer userFileTypeId);

    RekognitionProProcess getRekognitionProProcessById(int processId);

    RekognitionProProcess saveRekognitionProcess(int loanApplicationId, char type, int userFileTypeId, int userFileId, Character status, JSONObject response);

    RekognitionProProcess updateRekognitionProcess(int processId, JSONObject response, char status, String errorDetail);

    RekognitionProResult saveRekognitionProResult(int loanApplicationId, Character status, RekognitionProData response);

    RekognitionProResult getRekognitionProResult(int loanApplicationId);
    RekognitionReniecResult saveRekognitionReniecResult(int loanApplicationId, Character status, RekognitionReniecData response);
    RekognitionReniecResult getRekognitionReniecResult(int loanApplicationId);
    RekognitionProProcess getRekognitionProProcessByLoanIdUserFileIdAndTypeAndStatus(int loanApplicationId, char type, int userFileId, Character status);
}
