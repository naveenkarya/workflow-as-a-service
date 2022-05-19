package com.coen241.schedulerservice.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetryTaskRequest {
    private String taskId;
    private String workflowId;
}
