package com.wfaas.task2.services;

import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartTaskService {
	
	public String start() {
		System.out.println("Inside start() :::::: StartTaskService ");
		
		String msg = "Task #2 has started!";
		System.out.println(msg);
		
		JSONObject response = new JSONObject();
		response.put("msg", msg);
		response.put("status", "STARTED");
		
		System.out.println("Sending the response as Task started to Scheduler service...");
		
		return response.toString();
	}

}
