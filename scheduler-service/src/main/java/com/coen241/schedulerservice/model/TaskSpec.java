package com.coen241.schedulerservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import lombok.Data;

@Data
public class TaskSpec {
    @DynamoDBAttribute
    private String taskId;
    @DynamoDBAttribute
    private String serviceName;
    private String taskName;
    private int order;
}
