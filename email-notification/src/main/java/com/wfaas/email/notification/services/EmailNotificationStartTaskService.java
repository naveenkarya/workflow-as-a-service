package com.wfaas.email.notification.services;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import com.wfaas.email.notification.common.TaskStatus;

@Configuration
public class EmailNotificationStartTaskService {
	
	public String start(String workflowId, String taskId, Map<String, String> attributes) {
		JSONObject response = new JSONObject();
		response.put("workflowId", workflowId);
		response.put("taskId", taskId);
		response.put("attributes", attributes);
		
		try{
			System.out.println("\nInside start() :::::: EmailNotificationStartTaskService ");
			
			System.out.println("Email notification task has started!");
			response.put("status", TaskStatus.STARTED);
			
			System.out.println("Sending the status as 'STARTED' to Scheduler service... response="+response);
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in sendResponseToScheduler() ::::: EmailNotificationStartTaskService, "+e);
			e.printStackTrace();
			
			System.out.println("\nEmail notification task has failed!");
			response.put("status", TaskStatus.FAILED);
		}
		
		return response.toString();
	}

}
