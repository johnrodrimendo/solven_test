package com.affirm.common.service;

public interface PSqlErrorMessageService {

    String getMessageByErrorCode(String code, String stacktrace);

}
