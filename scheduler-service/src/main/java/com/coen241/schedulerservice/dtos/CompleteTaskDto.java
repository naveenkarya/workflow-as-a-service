package com.coen241.schedulerservice.dtos;

import com.coen241.schedulerservice.common.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompleteTaskDto {
    String workflowId;
    String taskId;
    Status status;
    String statusMessage;
    Map<String, String> attributes;
}
