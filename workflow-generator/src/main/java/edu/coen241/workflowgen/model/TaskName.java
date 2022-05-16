package edu.coen241.workflowgen.model;

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