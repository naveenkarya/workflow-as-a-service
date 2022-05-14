package com.wfaas.task2.controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wfaas.task2.services.StartTaskService;
import com.wfaas.task2.services.ExecuteTaskService;

@RestController
public class Task2Controller {
	
	private final static int NO_OF_THREADS = 10;
	
	@Autowired
	private StartTaskService startTaskService;
	
	@Autowired
	private JavaMailSenderImpl javaMailSender;
	
	
	private ExecutorService executorService;
	
	public Task2Controller() {
		System.out.println("Initialized the Fixed Thread Pool with "+NO_OF_THREADS);
		this.executorService = Executors.newFixedThreadPool(NO_OF_THREADS);
	}
	
	
	
	@RequestMapping(value = "/startTask", method = RequestMethod.POST)
	public String startTask(HttpServletRequest request) {
		try {
			Map<String, String[]> requestMap = request.getParameterMap(); 
			
			// Get workflowId, taskId, Attributes from the request
			String workflowId = requestMap.containsKey("workflowId") ? requestMap.get("workflowId")[0] : null;
			String taskId = requestMap.containsKey("taskId") ? requestMap.get("taskId")[0] : null;
			String attributesStr = requestMap.containsKey("attributes") ? requestMap.get("attributes")[0] : null;
			
			JSONObject attributes = new JSONObject(attributesStr);
			
			if(workflowId != null && workflowId.length() > 0 && taskId != null && taskId.length() > 0 &&
					attributes != null && attributes.length() > 0) {
				
				// Call Execute and complete Task  - New thread
				ExecuteTaskService emailService = new ExecuteTaskService(javaMailSender, workflowId, taskId, attributes);
				this.executorService.submit(emailService);
				
				
				// Send 200 - OK  (OR)  202 - Accepted
				return startTaskService.start();
			}
			else {
				System.out.println("Missing parameters - workflowId / taskId / attributes");
				return null;
			}
		}
		catch(Exception e) {
			System.out.println("Some error occurred in startTask() ::::: Task2Controller, "+e);
		}
		
		
		return null;
	}
	
	
	
	/**** TEST PURPOSE ****/
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		String str = "Salaam, Rocky Bhai";
		return str;
	}
	
	@RequestMapping(value = "testEmail", method = RequestMethod.GET)
	public String testEmail() {
		JSONObject attributes = new JSONObject();
		attributes.put("email", "karthikcm.77@gmail.com");
		
		ExecuteTaskService emailService = new ExecuteTaskService(javaMailSender, "123", "1", attributes);
		new Thread(emailService).start();
		
		return "Triggered email.. check inbox!";
	}
	
}
