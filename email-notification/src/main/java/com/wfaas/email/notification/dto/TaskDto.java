package com.wfaas.email.notification.dto;

import java.util.Map;

public class TaskDto {
	String workflowId;
	String taskId;
	Map<String, String> attributes;
	String status;
	String statusMessage;
	
	public TaskDto() {}
	
	public TaskDto(String workflowId, String taskId, Map<String, String> attributes, String status, String statusMessage) {
		super();
		this.workflowId = workflowId;
		this.taskId = taskId;
		this.attributes = attributes;
		this.status = status;
		this.statusMessage = statusMessage;
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

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
}
