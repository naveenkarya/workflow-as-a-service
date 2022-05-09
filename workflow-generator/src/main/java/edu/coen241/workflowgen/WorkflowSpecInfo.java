package edu.coen241.workflowgen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowSpecInfo {
    @Id
    private String id;
    private String name;
    private List<TaskOrder> taskOrderList;

}