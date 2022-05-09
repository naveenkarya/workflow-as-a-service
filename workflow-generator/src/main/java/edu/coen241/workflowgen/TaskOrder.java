package edu.coen241.workflowgen;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskOrder {
    private String taskId;
    private int order;
}