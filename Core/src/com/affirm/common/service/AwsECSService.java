package com.affirm.common.service;

import com.amazonaws.services.ecs.model.DescribeServicesResult;

public interface AwsECSService {

    DescribeServicesResult getECSStatus(String clusterName, String service);

}
