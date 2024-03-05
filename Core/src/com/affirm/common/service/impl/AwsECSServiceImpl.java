package com.affirm.common.service.impl;


import com.affirm.common.service.AwsECSService;
import com.affirm.common.service.EmailService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClient;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.DescribeServicesRequest;
import com.amazonaws.services.ecs.model.DescribeServicesResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class AwsECSServiceImpl implements AwsECSService {


    private static Logger logger = Logger.getLogger(AwsECSServiceImpl.class);

    private static final String AWS_ACCESSKEY = System.getenv("AWS_ACCESS_ECS_KEY_ID");
    private static final String AWS_SECRETKEY = System.getenv("AWS_SECRET_ECS_ACCESS_KEY");
    private static final Regions AWS_SES_REGION = Regions.US_EAST_1;

    private AmazonECS ecs;


    @PostConstruct
    public void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESSKEY, AWS_SECRETKEY);
        AWSStaticCredentialsProvider initialCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        ecs = AmazonECSClientBuilder.standard()
                .withCredentials(initialCredentialsProvider)
                .withRegion(AWS_SES_REGION)
                .build();
    }


    @Override
    public DescribeServicesResult getECSStatus(String clusterName, String service) {
        DescribeServicesRequest request = new DescribeServicesRequest();
        request.setCluster(clusterName);
        request.setServices(Arrays.asList(service));
        return ecs.describeServices(request);
    }
}
