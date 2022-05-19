package edu.coen241.workflowgen.mapper;

import edu.coen241.workflowgen.model.*;
import edu.coen241.workflowgen.repository.TaskInfoRepository;
import edu.coen241.workflowgen.repository.WorkflowSpecRepository;
import edu.coen241.workflowgen.service.K8SDeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class WorkflowResponseMapper {
    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    K8SDeploymentService k8SDeploymentService;
    @Autowired
    WorkflowSpecRepository workflowSpecRepository;
    public WorkflowSpecResponse mapToWorkflowResponse(WorkflowSpecInfo workflowSpecInfo) {
        WorkflowSpecResponse specResponse = new WorkflowSpecResponse();
        specResponse.setSpecId(workflowSpecInfo.getId());
        specResponse.setName(workflowSpecInfo.getName());
        specResponse.setDeploymentStatus(workflowSpecInfo.getDeploymentStatus().getValue());
        List<TaskSpecResponse> taskSpecList = new ArrayList<>();
        for(TaskOrder taskOrder : workflowSpecInfo.getTaskOrderList()) {
            Optional<TaskInfo> taskFromDBOp = taskInfoRepository.findById(taskOrder.getTaskId());
            taskFromDBOp.ifPresent(taskFromDB -> {
                TaskSpecResponse taskSpecResponse = TaskSpecResponse.builder()
                        .taskId(taskOrder.getTaskId())
                        .order(taskOrder.getOrder())
                        .nodePort(taskFromDB.getNodePort())
                        .taskName(taskFromDB.getTaskName())
                        .deploymentStatus(specResponse.getDeploymentStatus())
                        .serviceName(taskFromDB.getServiceName()).build();

                if(workflowSpecInfo.getDeploymentStatus() == DeploymentStatus.NOT_STARTED || workflowSpecInfo.getDeploymentStatus() == DeploymentStatus.DEPLOYED) {
                    specResponse.setDeploymentStatus(workflowSpecInfo.getDeploymentStatus().getValue());
                    taskSpecResponse.setDeploymentStatus(workflowSpecInfo.getDeploymentStatus().getValue());
                }
                else {
                    taskSpecResponse.setDeploymentStatus(k8SDeploymentService.getDeploymentStatus(taskSpecResponse.getServiceName()));
                }
                taskSpecList.add(taskSpecResponse);
            });
        }
        if(workflowSpecInfo.getDeploymentStatus() != DeploymentStatus.DEPLOYED
                && taskSpecList.stream().filter(taskSpec -> DeploymentStatus.DEPLOYED.getValue().equals(taskSpec.getDeploymentStatus())).count() == taskSpecList.size()) {
            workflowSpecInfo.setDeploymentStatus(DeploymentStatus.DEPLOYED);
            workflowSpecRepository.save(workflowSpecInfo);
            specResponse.setDeploymentStatus(DeploymentStatus.DEPLOYED.getValue());
        }
        specResponse.setTaskSpecList(taskSpecList);
        return specResponse;
    }
}
