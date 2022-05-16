package edu.coen241.workflowgen.model;

import edu.coen241.workflowgen.model.TaskSpecResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowSpecResponse {
    private String specId;
    private String name;
    private List<TaskSpecResponse> taskSpecList;
}
