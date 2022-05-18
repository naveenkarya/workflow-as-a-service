package edu.coen241.workflowgen.model;

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
    private String deploymentStatus;
    private List<TaskSpecResponse> taskSpecList;
}
