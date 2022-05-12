package com.coen241.schedulerservice.services;

import com.coen241.schedulerservice.common.Status;
import com.coen241.schedulerservice.model.Task;
import com.coen241.schedulerservice.model.Workflow;
import com.coen241.schedulerservice.model.WorkflowSpec;
import com.coen241.schedulerservice.repository.WorkflowRepository;
import com.coen241.schedulerservice.repository.WorkflowSpecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class SchedulerService {
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowSpecRepository workflowSpecRepository;

    //    TODO: Create a startWorkflow method which gets the tasks for workflowSpec and starts with 1st task
    public String startWorkflow(String workflowSpecId) {
        WorkflowSpec workflowSpec = workflowSpecRepository.findById(workflowSpecId);

        Workflow workflow = new Workflow();
        workflow.setWorkflowStatus(Status.IN_PROGRESS);
        workflow.setWorkflowSpecId(workflowSpec.getSpecId());

        List<Task> taskList = workflowSpec.getTaskList();
        Task firstTask = null;
        for (Task task : taskList) {
            task.setTaskStatus(Status.PENDING);
            if (Integer.parseInt(task.getTaskOrder()) == 1) {
                task.setTaskStatus(Status.IN_PROGRESS);
                firstTask = task;
            }
        }
        workflow.setTaskList(taskList);
//        workflow.setAttributesList();
        workflowRepository.save(workflow);

        startTask(Objects.requireNonNull(firstTask).getTaskId(), workflow.getWorkflowId());

        return "Task with task id: " + firstTask.getTaskId()
                + " started for the workflow id: " + workflow.getWorkflowId();
    }

//    TODO: Call the startTask API of the first task
    private String startTask(String taskId, String workflowId) {
        WebClient webClient = WebClient.create("http://localhost:3000");

        Mono<Task> completedTask = webClient.post()
                .uri("/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .attribute("taskId", taskId)
                .attribute("workflowId", workflowId)
                .retrieve()
                .bodyToMono(Task.class);

        return "Task with task id: " + taskId
                + " started for the workflow id: " + workflowId;
    }

    public String completeTask(String taskId, String workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId);
        Iterator<Task> taskIterator = workflow.getTaskList().iterator();
        Task nextTask = null;

        // Check if there are any tasks left for the current workflow
        while (taskIterator.hasNext()) {
            Task prevTask = taskIterator.next();
            if (prevTask.getTaskId().equals(taskId)) {
                prevTask.setTaskStatus(Status.COMPLETED);
                // Calling the start task for the next task
                if (taskIterator.hasNext()) {
                    nextTask = taskIterator.next();
                    nextTask.setTaskStatus(Status.IN_PROGRESS);
                    startTask(nextTask.getTaskId(), workflowId);
                } else {
                    workflow.setWorkflowStatus(Status.COMPLETED);
                    workflowRepository.update(workflowId, workflow);
                    return "Workflow is completed";
                }
            }
        }
        workflowRepository.update(workflowId, workflow);

        if (nextTask != null) {
            return "Task with task id: " + nextTask.getTaskId()
                    + " started for the workflow id: " + workflowId;
        } else {
            // TODO: Handle the exceptions
            return "Wrong workflow";
        }
    }
}
