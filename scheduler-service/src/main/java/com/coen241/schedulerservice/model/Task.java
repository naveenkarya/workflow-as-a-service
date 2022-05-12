package com.coen241.schedulerservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.coen241.schedulerservice.common.Status;
import lombok.Data;

@Data
@DynamoDBDocument
public class Task {
    @DynamoDBAttribute(attributeName = "id")
    private String taskId;
    @DynamoDBAttribute(attributeName = "order")
    private String taskOrder;
    @DynamoDBAttribute(attributeName = "status")
    private Status taskStatus;
}
