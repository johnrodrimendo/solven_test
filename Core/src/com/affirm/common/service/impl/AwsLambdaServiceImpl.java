package com.affirm.common.service.impl;


import com.affirm.common.service.AwsLambdaService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Service
public class AwsLambdaServiceImpl implements AwsLambdaService {


    private static Logger logger = Logger.getLogger(AwsLambdaServiceImpl.class);

    private static final String AWS_ACCESSKEY = System.getenv("AWS_ACCESS_LAMBDA_KEY_ID");
    private static final String AWS_SECRETKEY = System.getenv("AWS_SECRET_LAMBDA_ACCESS_KEY");
    private static final Regions AWS_LAMBDA_REGION = Regions.US_EAST_1;

    private AWSLambda lambdaClient;


    @PostConstruct
    public void init() {
        this.lambdaClient = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.AWS_ACCESSKEY, this.AWS_SECRETKEY)))
                .withRegion(this.AWS_LAMBDA_REGION)
                .build();
    }


    @Override
    public String callFunction(String functionName, String payload) {
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withInvocationType(InvocationType.RequestResponse)
                .withPayload(payload);
        InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);
        String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
        return new String(invokeResult.getPayload().array());
    }
}
