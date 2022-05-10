package edu.coen241.workflowgen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WorkflowRestController {
    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    WorkflowSpecRepository workflowSpecRepository;
    @Autowired
    TaskService taskService;

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
    public ResponseEntity<WorkflowSpecInfo> getWorkflowSpec(@PathVariable String workflowSpecId) {
        return ResponseEntity.ok(workflowSpecRepository.findById(workflowSpecId).get());
    }

}
