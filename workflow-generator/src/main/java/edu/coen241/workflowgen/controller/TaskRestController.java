package edu.coen241.workflowgen.controller;

import edu.coen241.workflowgen.model.DeploymentConfiguration;
import edu.coen241.workflowgen.model.TaskInfo;
import edu.coen241.workflowgen.model.TaskName;
import edu.coen241.workflowgen.repository.TaskInfoRepository;
import edu.coen241.workflowgen.service.K8SDeploymentService;
import edu.coen241.workflowgen.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskRestController {
    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    K8SDeploymentService k8SDeploymentService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addTask(@RequestBody TaskInfo taskInfo) {
        taskInfoRepository.save(taskInfo);
        Map<String, String> result = new HashMap<>();
        result.put("taskId", taskInfo.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskInfo> getTask(@PathVariable String taskId) {
        return ResponseEntity.ok(taskInfoRepository.findById(taskId).get());
    }

    @GetMapping
    public ResponseEntity<List<TaskInfo>> getTask() {
        return ResponseEntity.ok(taskInfoRepository.findAll());
    }

    @GetMapping("/names")
    public ResponseEntity<List<TaskName>> getTaskNames() {
        return ResponseEntity.ok(taskService.getTaskNames(taskInfoRepository.findAll()));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskInfoRepository.deleteById(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTasks() {
        taskInfoRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{taskSpecId}/deploy")
    public ResponseEntity<Void> deployTaskSpec(@PathVariable String taskSpecId) {
        Optional<TaskInfo> taskSpecOp = taskInfoRepository.findById(taskSpecId);
        if (taskSpecOp.isPresent()) {
            TaskInfo taskSpec = taskSpecOp.get();
            Integer nodePort = k8SDeploymentService.deployService(DeploymentConfiguration.fromTaskInfo(taskSpec));
            if (nodePort != null) {
                log.info("NodePort: " + nodePort);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{taskSpecId}/deploymentStatus")
    public ResponseEntity<Void> deploymentStatus(@PathVariable String taskSpecId) {
        Optional<TaskInfo> taskSpecOp = taskInfoRepository.findById(taskSpecId);
        if (taskSpecOp.isPresent()) {
            TaskInfo taskSpec = taskSpecOp.get();
            k8SDeploymentService.getDeploymentStatus(taskSpec.getServiceName());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
