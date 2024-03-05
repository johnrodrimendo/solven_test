package com.affirm.common.service.impl;

import com.affirm.common.service.PSqlErrorMessageService;
import org.springframework.stereotype.Service;

@Service("pSqlErrorMessageServiceImpl")
public class PSqlErrorMessageServiceImpl implements PSqlErrorMessageService {

    public static final String INVALID_TEXT_REPRESENTATION = "22P02";
    public static final String BAD_COPY_FILE_FORMAT_CODE = "22P04";

    @Override
    public String getMessageByErrorCode(String code, String stacktrace) {
        if(code == null) return null;
        String message = "";
        switch (code){
            case BAD_COPY_FILE_FORMAT_CODE:
                if(stacktrace != null){
                    String[] stacktraceSplit = stacktrace.split("\n");
                    if(stacktrace.contains("ERROR: missing data for column")){
                        message = stacktraceSplit[0].replace("ERROR: missing data for column","Falta información para la columna");
                        message+= " línea "+stacktraceSplit[1].split("line")[1];
                        return message;
                    }
                }
                break;
            case INVALID_TEXT_REPRESENTATION:
                if(stacktrace != null){
                    String[] stacktraceSplit = stacktrace.split("\n");
                    if(stacktrace.contains("ERROR: invalid input syntax for type")){
                        message = stacktraceSplit[0].replace("ERROR: invalid input syntax for type","Valor inválido para tipo");
                        message+= " línea "+stacktraceSplit[1].split("line")[1];
                        return message;
                    }
                }
                break;
            default:
                return "Oops, hubo un inconveniente con la carga del archivo, revisa el formato de los campos o escríbenos a dev@solven.pe";
        }
        return null;
    }
}
