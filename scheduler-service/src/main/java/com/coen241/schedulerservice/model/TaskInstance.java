package com.coen241.schedulerservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.coen241.schedulerservice.common.Status;
import lombok.Builder;
import lombok.Data;

@Data
@DynamoDBDocument
@Builder
public class TaskInstance {
    private String taskId;
    private String serviceName;
    @DynamoDBAttribute(attributeName = "order")
    private int order;
    @DynamoDBAttribute(attributeName = "status")
    private Status status;
}
