package edu.coen241.workflowgen;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskName {
    private String id;
    private String taskName;

}