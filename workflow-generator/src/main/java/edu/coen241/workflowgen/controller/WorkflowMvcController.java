package edu.coen241.workflowgen.controller;

import edu.coen241.workflowgen.model.TaskInfo;
import edu.coen241.workflowgen.model.WorkflowSpecInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WorkflowMvcController {

    @Autowired
    WorkflowRestController workflowRestController;

    @Autowired
    TaskRestController taskRestController;


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



    @GetMapping(value = "/viewWorkflows")
    public ModelAndView viewWorkflows(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewWorkflows");

        // Get list of all the workflows
        List<WorkflowSpecInfo> workflowSpecInfoList = workflowRestController.getAllWorkflowSpec().getBody();

        mv.addObject("msg", "hello");
        mv.addObject("workflowSpecs", workflowSpecInfoList);

        return mv;
    }


    @GetMapping(value = "/viewTasks")
    public ModelAndView viewTasks(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewTasks");

        // Get list of all the tasks
        List<TaskInfo> taskInfoList = taskRestController.getAllTasks().getBody();

        mv.addObject("msg", "hello");
        mv.addObject("taskInfo", taskInfoList);

        return mv;
    }
}
