package com.coen241.schedulerservice.services;

import com.coen241.schedulerservice.common.Status;
import com.coen241.schedulerservice.dtos.*;
import com.coen241.schedulerservice.model.*;
import com.coen241.schedulerservice.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
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
        workflow.setCreatedAt(Instant.now().toString());
        workflow.setUpdatedAt(workflow.getCreatedAt());
        Workflow createdWorkflow = workflowRepository.save(workflow);

        startTask(firstTask.getServiceName(), buildStartTaskRequest(createdWorkflow, firstTask), firstTask.getNodePort());
        return createdWorkflow.getWorkflowId();
    }

    private TaskInstance mapToPendingTaskInstance(TaskSpec taskSpec) {
        return TaskInstance.builder().taskId(taskSpec.getTaskId()).serviceName(taskSpec.getServiceName())
                .nodePort(taskSpec.getNodePort())
                .order(taskSpec.getOrder()).taskName(taskSpec.getTaskName()).status(Status.PENDING).build();
    }

    private StartTaskRequest buildStartTaskRequest(Workflow workflow, TaskInstance firstTask) {
        return StartTaskRequest.builder()
                .taskId(firstTask.getTaskId())
                .workflowId(workflow.getWorkflowId())
                .attributes(workflow.getAttributes())
                .build();
    }

    // Calls the startTask API of the given task
    private void startTask(String serviceName, StartTaskRequest startTaskRequest, Integer nodePort) {
        ResponseEntity<StartTaskResponse> startTaskResponse;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<StartTaskRequest> entity = new HttpEntity<>(startTaskRequest, headers);
        startTaskResponse = restTemplate.postForEntity(getStartTaskUrl(serviceName),
                entity, StartTaskResponse.class);
        String url = startTaskResponse.getBody() != null ? startTaskResponse.getBody().getUrl() : null;
        if (url != null) {
            if(nodePort != null) {
                url = "http://{{HOST}}:" + nodePort + url;
            }
            persistFormUrl(startTaskRequest, url);
        }
    }

    private String getStartTaskUrl(String serviceName) {
        return "http://" + serviceName + ":8080/startTask";
    }

    // Starts the next task if exists or completes the workflow
    public void completeTask(CompleteTaskDto completeTaskDto) {
        Workflow workflow = workflowRepository.findById(completeTaskDto.getWorkflowId());

        // Case when the current task has completed successfully
        if (completeTaskDto.getStatus() != Status.FAILED) {
            workflow.getTaskInstanceList().stream()
                    .filter(taskSpec -> taskSpec.getTaskId().equals(completeTaskDto.getTaskId()))
                    .forEach(taskInstance -> taskInstance.setStatus(Status.COMPLETED));
            // Copy all attributes from the response
            for (Map.Entry<String, String> attribute : completeTaskDto.getAttributes().entrySet()) {
                workflow.getAttributes().put(attribute.getKey(), attribute.getValue());
            }
            Optional<TaskInstance> nextPendingTask = workflow.getTaskInstanceList().stream()
                    .sorted(Comparator.comparingInt(TaskInstance::getOrder))
                    .filter(taskInstance -> taskInstance.getStatus().equals(Status.PENDING)).findFirst();
            // Start the next pending task if exists
            if (nextPendingTask.isPresent()) {
                TaskInstance next = nextPendingTask.get();
                next.setStatus(Status.IN_PROGRESS);
                startTask(next.getServiceName(), buildStartTaskRequest(workflow, next), next.getNodePort());
            } else { workflow.setWorkflowStatus(Status.COMPLETED); }
        // Case when the current task has failed execution
        } else {
            workflow.getTaskInstanceList().stream()
                    .filter(taskSpec -> taskSpec.getTaskId().equals(completeTaskDto.getTaskId()))
                    .forEach(taskInstance -> taskInstance.setStatus(Status.FAILED));
        }

        workflow.setUpdatedAt(Instant.now().toString());
        workflowRepository.update(workflow.getWorkflowId(), workflow);
    }

    // Persist Registration form url
    private void persistFormUrl(StartTaskRequest startTaskRequest, String url) {
        Workflow workflow = workflowRepository.findById(startTaskRequest.getWorkflowId());
        workflow.getTaskInstanceList().stream()
                .filter(taskInstance -> taskInstance.getTaskId().equals(startTaskRequest.getTaskId()))
                .findFirst().get().setUrl(url);
        workflow.setUpdatedAt(Instant.now().toString());
        workflowRepository.update(workflow.getWorkflowId(), workflow);
    }

    // Retry the task if it has failed
    public void retryTask(RetryTaskRequest retryTaskRequest) {
        Workflow workflow = workflowRepository.findById(retryTaskRequest.getWorkflowId());

        TaskInstance failedTask = workflow.getTaskInstanceList()
                .stream()
                .filter(taskInstance -> taskInstance.getTaskId().equals(retryTaskRequest.getTaskId()))
                .findFirst().get();

        failedTask.setStatus(Status.IN_PROGRESS);
        workflow.setUpdatedAt(Instant.now().toString());
        workflowRepository.update(workflow.getWorkflowId(), workflow);
        startTask(failedTask.getServiceName(),
                buildStartTaskRequest(workflow, failedTask), failedTask.getNodePort());

    }
}
