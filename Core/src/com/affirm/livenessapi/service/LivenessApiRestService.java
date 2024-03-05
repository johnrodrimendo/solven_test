package com.affirm.livenessapi.service;


import com.affirm.livenessapi.model.response.LivenessApiResponse;

public interface LivenessApiRestService {

    LivenessApiResponse getLivenessResponse(int loanApplicationId, Integer selfieUserFileId, Integer recordingUserFileId) throws Exception;

}
