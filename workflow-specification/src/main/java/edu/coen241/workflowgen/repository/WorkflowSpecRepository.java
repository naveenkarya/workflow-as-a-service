package edu.coen241.workflowgen.repository;

import edu.coen241.workflowgen.model.WorkflowSpecInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkflowSpecRepository extends MongoRepository<WorkflowSpecInfo, String> {


}