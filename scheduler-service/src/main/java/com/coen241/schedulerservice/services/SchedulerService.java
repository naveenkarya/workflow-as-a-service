package com.coen241.schedulerservice.services;

import com.coen241.schedulerservice.common.Status;
import com.coen241.schedulerservice.model.Task;
import com.coen241.schedulerservice.model.Workflow;
import com.coen241.schedulerservice.model.WorkflowSpec;
import com.coen241.schedulerservice.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SchedulerService {
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private RestTemplate restTemplate;

    public String startWorkflow(String workflowSpecId) {
        //TODO: Discover the url and config it in application config file
        WorkflowSpec workflowSpec = restTemplate.getForObject("" + workflowSpecId, WorkflowSpec.class);
        Workflow workflow = new Workflow();
        workflow.setWorkflowStatus(Status.IN_PROGRESS);
        workflow.setWorkflowSpecId(Objects.requireNonNull(workflowSpec).getSpecId());

        List<Task> taskList = workflowSpec.getTaskList();
        taskList.forEach(task -> task.setTaskStatus(Status.PENDING));
        Task firstTask = taskList.stream().filter(task -> task.getTaskOrder() == 1).findFirst().get();
        taskList.get(taskList.indexOf(firstTask)).setTaskStatus(Status.IN_PROGRESS);
        workflow.setTaskList(taskList);
        workflowRepository.save(workflow);

        startTask(Objects.requireNonNull(firstTask).getTaskId(), workflow.getWorkflowId());
        return workflow.getWorkflowId();
    }

    // Calls the startTask API of the given task id
    private void startTask(String taskId, String workflowId) { // How to send workflowId?
        restTemplate.getForObject("" + taskId, Task.class); //TODO: Send attributes as well
    }

    // Starts the next task if exists or completes the workflow
    public void completeTask(String taskId, String workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId);
        workflow.getTaskList().stream().filter(task -> task.getTaskId().equals(taskId))
                .forEach(task -> task.setTaskStatus(Status.COMPLETED));

        Optional<Task> nextTask = workflow.getTaskList().stream()
                .sorted(Comparator.comparingInt(Task::getTaskOrder))
                .filter(task -> task.getTaskStatus().equals(Status.PENDING)).findFirst();

        if (nextTask.isPresent()) {
            workflow.getTaskList().stream().filter(task -> task.equals(nextTask.get()))
                    .forEach(task -> task.setTaskStatus(Status.IN_PROGRESS));
            startTask(nextTask.get().getTaskId(), workflowId);
        } else {
            workflow.setWorkflowStatus(Status.COMPLETED);
        }
        workflowRepository.update(workflowId, workflow);
    }
}
