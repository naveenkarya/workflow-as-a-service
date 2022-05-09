package edu.coen241.workflowgen;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskInfoRepository extends MongoRepository<TaskInfo, String> {


}