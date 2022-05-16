package edu.coen241.workflowgen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkflowMvcController {

    @GetMapping(value = {"/", "/dashboard"})
    public String dashboard() {
        return "dashboard";
    }


    @GetMapping(value = "/createWorkflow")
    public String createWorkflow() {
        return "createWorkflow";
    }

    @GetMapping(value = "/createTask")
    public String createTask() {
        return "createTask";
    }

}
