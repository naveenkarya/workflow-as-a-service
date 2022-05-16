package com.wfaas.task2.dto;

import java.util.Map;

public class TaskDto {
	String workflowId;
	String taskId;
	Map<String, String> attributes;
	
	public TaskDto() {}
	
	public TaskDto(String workflowId, String taskId, Map<String, String> attributes) {
		super();
		this.workflowId = workflowId;
		this.taskId = taskId;
		this.attributes = attributes;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
}
