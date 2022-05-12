package com.coen241.schedulerservice.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.coen241.schedulerservice.model.WorkflowSpec;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkflowSpecRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public WorkflowSpecRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public WorkflowSpec save(WorkflowSpec workflowSpec) {
        dynamoDBMapper.save(workflowSpec);
        return workflowSpec;
    }

    public WorkflowSpec findById(String id) {
        return dynamoDBMapper.load(WorkflowSpec.class, id);
    }

    public List<WorkflowSpec> findAll() {
        return dynamoDBMapper.scan(WorkflowSpec.class, new DynamoDBScanExpression());
    }

    public WorkflowSpec update(String id, WorkflowSpec workflowSpec) {
        dynamoDBMapper.save(workflowSpec,
                                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(id)
                                )));
        return workflowSpec;
    }

    public String delete(String id) {
        dynamoDBMapper.delete(id);
        return "Workflow specification with id: " + id + " deleted successfully";
    }
}
