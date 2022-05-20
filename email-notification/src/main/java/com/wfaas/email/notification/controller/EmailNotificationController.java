package com.wfaas.email.notification.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wfaas.email.notification.common.TaskStatus;
import com.wfaas.email.notification.dto.TaskDto;
import com.wfaas.email.notification.services.EmailNotificationExecuteTaskService;
import com.wfaas.email.notification.services.EmailNotificationStartTaskService;

@RestController
public class EmailNotificationController {
	
	private final static int NO_OF_THREADS = 10;
	
	@Autowired
	private EmailNotificationStartTaskService startTaskService;
	
	@Autowired
	private JavaMailSenderImpl javaMailSender;
	
	
	private ExecutorService executorService;
	
	public EmailNotificationController() {
		System.out.println("Initialized the Fixed Thread Pool with "+NO_OF_THREADS);
		this.executorService = Executors.newFixedThreadPool(NO_OF_THREADS);
	}
	
	
	
	@RequestMapping(value = "/startTask", method = RequestMethod.POST)
	public String startTask(@RequestBody TaskDto taskDto) {
		System.out.println("\nInside startTask() :::::: EmailNotificationController ");
		
		String workflowId = taskDto.getWorkflowId();
		String taskId = taskDto.getTaskId();
		String status = taskDto.getStatus();
		Map<String, String> attributes = taskDto.getAttributes();
		
		System.out.println("Parameters: workflowId="+workflowId +", taskId="+taskId +", status="+status +", attributes="+attributes);
		
		try {
			if(workflowId != null && workflowId.length() > 0 && taskId != null && taskId.length() > 0 && attributes != null && attributes.size() > 0) {
				
				// Call and execute Email task service (New thread)
				EmailNotificationExecuteTaskService emailService = new EmailNotificationExecuteTaskService(javaMailSender, workflowId, taskId, attributes);
				this.executorService.submit(emailService);
				
				// Send 200 OK Response to Scheduler service
				return startTaskService.start(workflowId, taskId, attributes);
			}
			else {
				System.out.println("Missing parameters - workflowId / taskId / attributes");
				return sendErrorResponse(workflowId, taskId, attributes);
			}
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in startTask() ::::: EmailNotificationController, "+e);
			e.printStackTrace();
			return sendErrorResponse(workflowId, taskId, attributes);
		}
	}
	
	
	public String sendErrorResponse(String workflowId, String taskId, Map<String, String> attributes) {
		JSONObject response = new JSONObject();
		response.put("workflowId", workflowId);
		response.put("taskId", taskId);
		response.put("attributes", attributes);
		response.put("status", TaskStatus.FAILED);
		return response.toString();
	}
	
	
	
	
	/**** TEST PURPOSE ****/
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		String str = "Salaam, Rocky Bhai";
		return str;
	}
	
	@RequestMapping(value = "testEmail", method = RequestMethod.GET)
	public String testEmail() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("email", "karthikcm.77@gmail.com");
		
		EmailNotificationExecuteTaskService emailService = new EmailNotificationExecuteTaskService(javaMailSender, "123", "1", attributes);
		new Thread(emailService).start();
		
		return "Triggered email.. check inbox!";
	}
	
}
