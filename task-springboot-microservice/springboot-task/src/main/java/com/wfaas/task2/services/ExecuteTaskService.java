package com.wfaas.task2.services;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ExecuteTaskService implements Runnable {
	
	private String workflowId;
	private String taskId;
	private JSONObject attributes;
	
	public ExecuteTaskService(String workflowId, String taskId, JSONObject attributes){
		this.workflowId = workflowId;
		this.taskId = taskId;
		this.attributes = attributes;
	}
	
	
	@Override
	public void run() {
		executeTask();
		sendResponseToScheduler(workflowId, taskId, attributes);
	}
	
	public String executeTask() {
		try{
			Thread.sleep(5000);
		}
		catch(Exception e) {
			
		}
		
		System.out.println("<<::::: From Spring Boot Microservice task :::::>>");
		
		String output = "Hello, World 2";
		System.out.println("Task #2 output - "+output);
		
		String response = "Task #2 is successfully completed!";
		System.out.println(response);
		return response;
	}
	
	public void sendResponseToScheduler(String workflowId, String taskId, JSONObject attributes) {
		String URL = taskId +"-service:8080/completeTask/";
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
			
			JSONObject body = new JSONObject();
			body.put("workflowId", workflowId);
			body.put("taskId", taskId);
			body.put("attributes", attributes);
			
			body.put("status", "TASK_COMPLETED");
			body.put("msg", "Task #2 is successfully completed");
			
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> requestEntity = new HttpEntity<String>(body.toString(), headers);
			
			// Send request (response) to Scheduler Service 
			ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
			
			String response = responseEntity.getBody();
			System.out.println("Response = "+response);
		}
		catch(Exception e) {
			System.out.println("Some error occurred in sendResponseToScheduler() ::::: ExecuteTaskService, "+e);
		}
		
	}
}
