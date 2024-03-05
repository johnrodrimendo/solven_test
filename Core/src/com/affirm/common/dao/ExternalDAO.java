package com.affirm.common.dao;

import com.affirm.bantotalrest.model.BantotalToken;
import com.affirm.common.model.ExternalWSRecord;

import java.util.Date;
import java.util.List;

public interface ExternalDAO {

    void insertBantotalToken(String token);

    BantotalToken getBantotalToken();

}
