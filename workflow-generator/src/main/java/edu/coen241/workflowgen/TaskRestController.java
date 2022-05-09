package edu.coen241.workflowgen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TaskRestController {
    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    TaskService taskService;

    @PostMapping("/task/add")
    public ResponseEntity<Map<String, String>> addTask(@RequestBody TaskInfo taskInfo) {
        taskInfoRepository.save(taskInfo);
        Map<String, String> result = new HashMap<>();
        result.put("taskId", taskInfo.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<TaskInfo> getTask(@PathVariable String taskId) {
        return ResponseEntity.ok(taskInfoRepository.findById(taskId).get());
    }
    @GetMapping("/taskNames")
    public ResponseEntity<List<TaskName>> getTaskNames() {
        return ResponseEntity.ok(taskService.getTaskNames(taskInfoRepository.findAll()));
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskInfoRepository.deleteById(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
