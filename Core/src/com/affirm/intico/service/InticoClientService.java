package com.affirm.intico.service;

import com.affirm.intico.model.InticoConfirmSmsResponse;
import com.affirm.intico.model.InticoSendSmsResponse;

import java.io.IOException;

public interface InticoClientService {

    InticoSendSmsResponse sendInticoSms(String phoneNumber, String message) throws IOException;

    InticoConfirmSmsResponse verifySmsStatus(String code) throws IOException;
}
