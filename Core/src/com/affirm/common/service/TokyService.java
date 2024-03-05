package com.affirm.common.service;

import org.apache.commons.lang3.tuple.Triple;

import java.util.Date;

public interface TokyService {

    Triple<Integer, Integer, Integer> getCallsAndMinutes(Date from, Date to, String sysUserEmail) throws Exception;
}
