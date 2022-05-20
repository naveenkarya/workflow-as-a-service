package com.wfaas.email.notification.dto;

import java.util.Map;

public class TaskDto {
	String workflowId;
	String taskId;
	Map<String, String> attributes;
	String status;
	
	public TaskDto() {}
	
	public TaskDto(String workflowId, String taskId, Map<String, String> attributes, String status) {
		super();
		this.workflowId = workflowId;
		this.taskId = taskId;
		this.attributes = attributes;
		this.status = status;
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
	public String getStatus() {
		return status;
	}
}
