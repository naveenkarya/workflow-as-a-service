package com.coen241.schedulerservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.coen241.schedulerservice.common.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@DynamoDBDocument
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstance {
    @DynamoDBAttribute
    private String taskId;
    @DynamoDBAttribute
    private String serviceName;
    @DynamoDBAttribute
    private int order;
    @DynamoDBAttribute
    private String taskName;
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute
    private Status status;
    @DynamoDBAttribute
    private String url;
}
