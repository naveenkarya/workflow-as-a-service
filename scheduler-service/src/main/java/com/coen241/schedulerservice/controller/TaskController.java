package com.coen241.schedulerservice.controller;

import com.coen241.schedulerservice.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TaskController {

    @Autowired
    SchedulerService schedulerService;

    @RequestMapping(value = "completeTask", method = RequestMethod.PUT)
    @ResponseBody
    public String completeTask(HttpServletRequest request) {
        String workflowId = request.getParameter("workflowId");
        String taskId = request.getParameter("taskId");
        return schedulerService.completeTask(taskId, workflowId);
    }

}
