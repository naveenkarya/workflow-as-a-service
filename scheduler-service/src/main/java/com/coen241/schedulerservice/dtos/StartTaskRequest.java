package com.coen241.schedulerservice.dtos;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartTaskRequest {
    private String taskId;
    private String workflowId;
    private Map<String, String> attributes;
}
