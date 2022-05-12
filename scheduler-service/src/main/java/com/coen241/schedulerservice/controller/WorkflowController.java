package com.coen241.schedulerservice.controller;

import com.coen241.schedulerservice.dtos.WorkflowResponse;
import com.coen241.schedulerservice.model.Workflow;
import com.coen241.schedulerservice.repository.WorkflowRepository;
import com.coen241.schedulerservice.services.SchedulerService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WorkflowController {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private WorkflowRepository workflowRepository;

    @GetMapping(value = {"/","/workflows"})
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflowList = workflowRepository.findAll();
        return new ResponseEntity<>(workflowList, HttpStatus.OK);
    }

    @GetMapping("/workflows/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable @NonNull String id) {
        Workflow workflow = workflowRepository.findById(id);
        return new ResponseEntity<>(workflow, HttpStatus.OK);
    }

    @PostMapping("/startWorkflow")
    public ResponseEntity<WorkflowResponse> startWorkflow(@Validated @RequestBody String workflowSpecId) {
        String workflowId = schedulerService.startWorkflow(workflowSpecId);

        WorkflowResponse workflowResponse = new WorkflowResponse();
        workflowResponse.setWorkflowId(workflowId);
        return new ResponseEntity<>(workflowResponse, HttpStatus.OK);
    }
}
