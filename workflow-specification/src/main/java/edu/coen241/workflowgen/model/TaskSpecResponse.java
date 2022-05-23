package edu.coen241.workflowgen.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskSpecResponse {
    private String taskId;
    private String serviceName;
    private String taskName;
    private int order;
    private Integer nodePort;
    private String deploymentStatus;
}
