package edu.coen241.workflowgen.repository;

import edu.coen241.workflowgen.model.TaskInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskInfoRepository extends MongoRepository<TaskInfo, String> {


}