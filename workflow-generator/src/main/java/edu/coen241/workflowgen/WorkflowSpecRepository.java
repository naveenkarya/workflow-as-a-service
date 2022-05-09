package edu.coen241.workflowgen;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkflowSpecRepository extends MongoRepository<WorkflowSpecInfo, String> {


}