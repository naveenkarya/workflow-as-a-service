package com.coen241.schedulerservice.controller;

import com.coen241.schedulerservice.dtos.WorkflowResponse;
import com.coen241.schedulerservice.dtos.CreateWorkflowRequest;
import com.coen241.schedulerservice.model.Workflow;
import com.coen241.schedulerservice.repository.WorkflowRepository;
import com.coen241.schedulerservice.services.SchedulerService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private WorkflowRepository workflowRepository;

    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflowList = workflowRepository.findAll();
        return new ResponseEntity<>(workflowList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable @NonNull String id) {
        Workflow workflow = workflowRepository.findById(id);
        return new ResponseEntity<>(workflow, HttpStatus.OK);
    }

    @PostMapping("/start")
    public ResponseEntity<WorkflowResponse> startWorkflow(@RequestBody CreateWorkflowRequest createWorkflowRequest) {
        String workflowId = schedulerService.startWorkflow(createWorkflowRequest);
        WorkflowResponse workflowResponse = new WorkflowResponse();
        workflowResponse.setWorkflowId(workflowId);
        return new ResponseEntity<>(workflowResponse, HttpStatus.OK);
    }
    @PostMapping("/{id}/update")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable @NonNull String id, @RequestBody CreateWorkflowRequest createWorkflowRequest) {
        Workflow workflow = workflowRepository.findById(id);
        if(workflow == null) {
            return ResponseEntity.notFound().build();
        }
        if(!CollectionUtils.isEmpty(createWorkflowRequest.getAttributes())) {
            if(workflow.getAttributes() == null) {
                workflow.setAttributes(new HashMap<>());
            }
            for(Map.Entry<String, String> attr : createWorkflowRequest.getAttributes().entrySet()) {
                workflow.getAttributes().put(attr.getKey(), attr.getValue());
            }
        }
        workflowRepository.update(id, workflow);
        return new ResponseEntity<>(workflow, HttpStatus.OK);
    }
}
