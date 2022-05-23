package edu.coen241.workflowgen.controller;

import edu.coen241.workflowgen.mapper.WorkflowResponseMapper;
import edu.coen241.workflowgen.model.*;
import edu.coen241.workflowgen.repository.TaskInfoRepository;
import edu.coen241.workflowgen.repository.WorkflowSpecRepository;
import edu.coen241.workflowgen.service.K8SDeploymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static edu.coen241.workflowgen.model.DeploymentConfiguration.SCHEDULER_CONFIG;
import static edu.coen241.workflowgen.model.DeploymentConfiguration.WORKFLOW_UI_CONFIG;

@Slf4j
@RestController
@RequestMapping("/workflowSpec")
public class WorkflowRestController {

    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    WorkflowSpecRepository workflowSpecRepository;
    @Autowired
    K8SDeploymentService k8SDeploymentService;
    @Autowired
    WorkflowResponseMapper workflowResponseMapper;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createWorkflowSpec(@RequestBody WorkflowSpecInfo workflowSpecInfo) {
        workflowSpecRepository.save(workflowSpecInfo);
        Map<String, String> result = new HashMap<>();
        result.put("workflowSpecId", workflowSpecInfo.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<WorkflowSpecInfo>> getAllWorkflowSpec() {
        return ResponseEntity.ok(workflowSpecRepository.findAll());
    }

    @GetMapping("/getAllWorkflows")
    public ResponseEntity<List<WorkflowSpecResponse>> getAllWorkflowSpecResponse() {
        List<WorkflowSpecResponse> list = workflowSpecRepository.findAll().stream()
                .map(specInfo -> workflowResponseMapper.mapToWorkflowResponse(specInfo))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{workflowSpecId}")
    public ResponseEntity<WorkflowSpecResponse> getWorkflowSpec(@PathVariable String workflowSpecId) {
        Optional<WorkflowSpecInfo> workflowSpecOp = workflowSpecRepository.findById(workflowSpecId);
        if (workflowSpecOp.isPresent()) {
            return ResponseEntity.ok(workflowResponseMapper.mapToWorkflowResponse(workflowSpecOp.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllWorkflowSpecs() {
        workflowSpecRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{workflowSpecId}/deploy")
    public ResponseEntity<Void> deployWorkflowSpec(@PathVariable String workflowSpecId) {
        Optional<WorkflowSpecInfo> workflowSpecOp = workflowSpecRepository.findById(workflowSpecId);
        if (workflowSpecOp.isPresent()) {
            WorkflowSpecInfo workflowSpec = workflowSpecOp.get();
            workflowSpec.setDeploymentStatus(DeploymentStatus.IN_PROGRESS);
            workflowSpecRepository.save(workflowSpec);
            Iterable<TaskInfo> allTasks = taskInfoRepository.findAllById(workflowSpec.getTaskOrderList().stream().map(TaskOrder::getTaskId).collect(Collectors.toList()));
            int k = 0;
            for (TaskInfo taskInfo : allTasks) {
                k++;
                Integer nodePort = k8SDeploymentService.deployService(DeploymentConfiguration.fromTaskInfo(taskInfo));
                if (nodePort != null) {
                    log.info("NodePort for {}: {}", taskInfo.getServiceName(), nodePort);
                }
            }
            if (k > 0) {
                log.info("Workflow Spec not empty. Deploying scheduler and ui.");
                k8SDeploymentService.deployService(SCHEDULER_CONFIG);
                Integer nodePortUI = k8SDeploymentService.deployService(WORKFLOW_UI_CONFIG);
                if (nodePortUI != null) {
                    log.info("NodePortUI: " + nodePortUI);
                }
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
