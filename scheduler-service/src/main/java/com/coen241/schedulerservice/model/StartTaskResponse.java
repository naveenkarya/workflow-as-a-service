package com.coen241.schedulerservice.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartTaskResponse {
    private String taskId;
    private String workflowId;
    private Map<String, String> attributes;
}
