package edu.coen241.workflowgen.model;

import edu.coen241.workflowgen.model.TaskOrder;
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
    private String deploymentStatus = "Not Deployed";
    private List<TaskOrder> taskOrderList;
}