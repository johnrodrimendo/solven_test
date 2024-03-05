    package com.affirm.aws.lambda;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Service
public class AWSLambdaClient {

    private AWSLambda client;

    @PostConstruct
    public void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"));
        AWSStaticCredentialsProvider initialCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(initialCredentialsProvider);

        client = builder.build();
    }

    public <T> T invokeLambda(String lambdaName, String payload, Class<T> returnType) throws Exception {
        InvokeRequest request = new InvokeRequest().withFunctionName(lambdaName).withPayload(payload);
        InvokeResult result = client.invoke(request);
        JSONObject json = new JSONObject(new String(result.getPayload().array(), StandardCharsets.UTF_8));
        if(json.has("body")) {
            JSONObject jsonBody = new JSONObject(json.getString("body"));

            if (json.getInt("statusCode") >= 400) {
                throw new Exception(jsonBody.has("error") ? jsonBody.getString("error") : jsonBody.toString());
            }

            return new Gson().fromJson(jsonBody.toString(), returnType);
        } else {
            throw new Exception(json.getString("errorMessage"));
        }
    }

}
