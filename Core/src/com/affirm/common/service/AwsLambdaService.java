package com.affirm.common.service;

public interface AwsLambdaService {
    public abstract String callFunction(String functionName, String payload);
}
