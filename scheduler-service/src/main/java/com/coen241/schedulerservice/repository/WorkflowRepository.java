package com.coen241.schedulerservice.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.coen241.schedulerservice.model.Workflow;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkflowRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public WorkflowRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Workflow save(Workflow workflow) {
        dynamoDBMapper.save(workflow);
        return workflow;
    }

    public Workflow findById(String id) {
        return dynamoDBMapper.load(Workflow.class, id);
    }

    public List<Workflow> findAll() {
        return dynamoDBMapper.scan(Workflow.class, new DynamoDBScanExpression());
    }

    public Workflow update(String id, Workflow workflow) {
        dynamoDBMapper.save(workflow,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(id)
                                )));
        return workflow;
    }

    public String delete(String id) {
        dynamoDBMapper.delete(id);
        return "Workflow with id: " + id + " deleted successfully";
    }

}
