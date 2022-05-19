package com.coen241.schedulerservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkflowRequest {
    private String workflowSpecId;
    private Map<String, String> attributes;
}
