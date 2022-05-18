package edu.coen241.workflowgen.mapper;

import edu.coen241.workflowgen.model.*;
import edu.coen241.workflowgen.repository.TaskInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class WorkflowResponseMapper {
    @Autowired
    TaskInfoRepository taskInfoRepository;
    public WorkflowSpecResponse mapToWorkflowResponse(WorkflowSpecInfo workflowSpecInfo) {
        WorkflowSpecResponse specResponse = new WorkflowSpecResponse();
        specResponse.setSpecId(workflowSpecInfo.getId());
        specResponse.setName(workflowSpecInfo.getName());
        specResponse.setDeploymentStatus(workflowSpecInfo.getDeploymentStatus().toString());
        List<TaskSpecResponse> taskSpecList = new ArrayList<>();
        for(TaskOrder taskOrder : workflowSpecInfo.getTaskOrderList()) {
            Optional<TaskInfo> taskFromDBOp = taskInfoRepository.findById(taskOrder.getTaskId());
            taskFromDBOp.ifPresent(taskFromDB -> taskSpecList.add(TaskSpecResponse.builder()
                    .taskId(taskOrder.getTaskId())
                    .order(taskOrder.getOrder())
                    .nodePort(taskFromDB.getNodePort())
                    .taskName(taskFromDB.getTaskName())
                    .deploymentStatus(specResponse.getDeploymentStatus())
                    .serviceName(taskFromDB.getServiceName()).build()));
        }
        specResponse.setTaskSpecList(taskSpecList);
        return specResponse;
    }
}
