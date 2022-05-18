package edu.coen241.workflowgen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WorkflowSpecInfo {
    @Id
    private String id;
    private String name;
    private DeploymentStatus deploymentStatus = DeploymentStatus.NOT_STARTED;
    private List<TaskOrder> taskOrderList;

}