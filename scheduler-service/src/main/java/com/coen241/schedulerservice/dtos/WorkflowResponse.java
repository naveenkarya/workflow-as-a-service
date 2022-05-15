package com.coen241.schedulerservice.dtos;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class WorkflowResponse {
    private String workflowId;
    private String url;
}
