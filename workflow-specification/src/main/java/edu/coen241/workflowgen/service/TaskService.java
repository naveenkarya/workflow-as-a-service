package edu.coen241.workflowgen.service;

import edu.coen241.workflowgen.model.TaskInfo;
import edu.coen241.workflowgen.model.TaskName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    public List<TaskName> getTaskNames(List<TaskInfo> allTasks) {
        return allTasks.stream().map(task -> TaskName.builder().id(task.getId()).taskName(task.getTaskName()).build()).collect(Collectors.toList());
    }
}
