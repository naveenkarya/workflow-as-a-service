package com.coen241.schedulerservice.services;

import com.coen241.schedulerservice.common.Status;
import com.coen241.schedulerservice.dtos.CompleteTaskDto;
import com.coen241.schedulerservice.model.*;
import com.coen241.schedulerservice.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WorkflowSpecService workflowSpecService;

    public String startWorkflow(CreateWorkflowRequest createWorkflowRequest) {
        WorkflowSpec workflowSpec = workflowSpecService.getWorkflowSpec(createWorkflowRequest.getWorkflowSpecId());
        Workflow workflow = new Workflow();
        workflow.setWorkflowStatus(Status.IN_PROGRESS);
        workflow.setWorkflowSpecId(Objects.requireNonNull(workflowSpec).getSpecId());

        List<TaskInstance> taskInstanceList = workflowSpec.getTaskSpecList()
                .stream()
                .map(this::mapToPendingTaskInstance)
                .collect(Collectors.toList());

        TaskInstance firstTask = taskInstanceList.stream()
                .filter(taskSpec -> taskSpec.getOrder() == 1)
                .findFirst()
                .get();

        firstTask.setStatus(Status.IN_PROGRESS);
        workflow.setWorkflowStatus(Status.IN_PROGRESS);
        workflow.setWorkflowSpecId(createWorkflowRequest.getWorkflowSpecId());
        workflow.setAttributes(createWorkflowRequest.getAttributes());
        workflow.setTaskInstanceList(taskInstanceList);
        workflow.setName(workflowSpec.getName());
        Workflow createdWorkflow = workflowRepository.save(workflow);

        startTask(firstTask.getServiceName(), buildStartTaskRequest(createdWorkflow, firstTask));
        return createdWorkflow.getWorkflowId();
    }

    private TaskInstance mapToPendingTaskInstance(TaskSpec taskSpec) {
        return TaskInstance.builder().taskId(taskSpec.getTaskId()).serviceName(taskSpec.getServiceName())
                .order(taskSpec.getOrder()).taskName(taskSpec.getTaskName()).status(Status.PENDING).build();
    }

    private StartTaskRequest buildStartTaskRequest(Workflow workflow, TaskInstance firstTask) {
        return StartTaskRequest.builder()
                .taskId(firstTask.getTaskId())
                .workflowId(workflow.getWorkflowId())
                .attributes(workflow.getAttributes())
                .build();
    }

    // Calls the startTask API of the given task id
    private void startTask(String serviceName, StartTaskRequest startTaskRequest) {
        ResponseEntity<StartTaskResponse> startTaskResponse;
        startTaskResponse = restTemplate.postForEntity(serviceName + "/startTask",
                                                            startTaskRequest, StartTaskResponse.class);

        String url = startTaskResponse.getBody() != null ? startTaskResponse.getBody().getUrl() : null;
        if (url != null) { persistFormUrl(startTaskRequest, url); }
    }

    // Starts the next task if exists or completes the workflow
    public void completeTask(CompleteTaskDto completeTaskDto) {
        Workflow workflow = workflowRepository.findById(completeTaskDto.getWorkflowId());
        workflow.getTaskInstanceList().stream()
                .filter(taskSpec -> taskSpec.getTaskId().equals(completeTaskDto.getTaskId()))
                .forEach(taskInstance -> taskInstance.setStatus(Status.COMPLETED));

        Optional<TaskInstance> nextTask = workflow.getTaskInstanceList().stream()
                .sorted(Comparator.comparingInt(TaskInstance::getOrder))
                .filter(taskInstance -> taskInstance.getStatus().equals(Status.PENDING)).findFirst();

        if (nextTask.isPresent()) {
            TaskInstance next = nextTask.get();
            next.setStatus(Status.IN_PROGRESS);
            startTask(next.getServiceName(), buildStartTaskRequest(workflow, next));
        } else {
            workflow.setWorkflowStatus(Status.COMPLETED);
        }
        workflowRepository.update(workflow.getWorkflowId(), workflow);
    }

    // Persist Registration form url
    private void persistFormUrl(StartTaskRequest startTaskRequest, String url) {
        Workflow workflow = workflowRepository.findById(startTaskRequest.getWorkflowId());
        workflow.getTaskInstanceList().stream()
                .filter(taskInstance -> taskInstance.getTaskId().equals(startTaskRequest.getTaskId()))
                .findFirst().get().setUrl(url);
        workflowRepository.update(workflow.getWorkflowId(), workflow);
    }
}
