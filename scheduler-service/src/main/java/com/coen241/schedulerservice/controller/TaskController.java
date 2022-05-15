package com.coen241.schedulerservice.controller;

import com.coen241.schedulerservice.dtos.CompleteTaskDto;
import com.coen241.schedulerservice.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    SchedulerService schedulerService;

    @PostMapping("/complete")
    public ResponseEntity<Void> completeTask(@RequestBody CompleteTaskDto completeTaskDto) {
        String workflowId = completeTaskDto.getWorkflowId();
        String taskId = completeTaskDto.getTaskId();
        schedulerService.completeTask(taskId, workflowId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
