package edu.coen241.workflowgen.controller;

import edu.coen241.workflowgen.mapper.WorkflowResponseMapper;
import edu.coen241.workflowgen.model.WorkflowSpecInfo;
import edu.coen241.workflowgen.model.WorkflowSpecResponse;
import edu.coen241.workflowgen.repository.TaskInfoRepository;
import edu.coen241.workflowgen.repository.WorkflowSpecRepository;
import edu.coen241.workflowgen.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class WorkflowRestController {
    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    WorkflowSpecRepository workflowSpecRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    WorkflowResponseMapper workflowResponseMapper;

    @PostMapping("/workflowSpec/create")
    public ResponseEntity<Map<String, String>> createWorkflowSpec(@RequestBody WorkflowSpecInfo workflowSpecInfo) {
        workflowSpecRepository.save(workflowSpecInfo);
        Map<String, String> result = new HashMap<>();
        result.put("workflowSpecId", workflowSpecInfo.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflowSpec")
    public ResponseEntity<List<WorkflowSpecInfo>> getAllWorkflowSpec() {
        return ResponseEntity.ok(workflowSpecRepository.findAll());
    }

    @GetMapping("/workflowSpec/{workflowSpecId}")
    public ResponseEntity<WorkflowSpecResponse> getWorkflowSpec(@PathVariable String workflowSpecId) {
        Optional<WorkflowSpecInfo> workflowSpecOp = workflowSpecRepository.findById(workflowSpecId);
        if (workflowSpecOp.isPresent()) {
            return ResponseEntity.ok(workflowResponseMapper.mapToWorkflowResponse(workflowSpecOp.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/workflowSpec")
    public ResponseEntity<Void> deleteAllWorkflowSpecs() {
        workflowSpecRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
