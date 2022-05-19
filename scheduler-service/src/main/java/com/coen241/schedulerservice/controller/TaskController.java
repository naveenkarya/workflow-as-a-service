package com.coen241.schedulerservice.controller;

import com.coen241.schedulerservice.dtos.CompleteTaskDto;
import com.coen241.schedulerservice.dtos.RetryTaskRequest;
import com.coen241.schedulerservice.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    SchedulerService schedulerService;

    @PostMapping("/complete")
    public ResponseEntity<Void> completeTask(@RequestBody CompleteTaskDto completeTaskDto) {
        schedulerService.completeTask(completeTaskDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/retry")
    public ResponseEntity<Void> retryTask(@RequestBody RetryTaskRequest retryTaskRequest) {
        schedulerService.retryTask(retryTaskRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
