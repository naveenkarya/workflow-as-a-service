package com.wfaas.task2.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wfaas.task2.dto.TaskDto;
import com.wfaas.task2.services.ExecuteTaskService;
import com.wfaas.task2.services.StartTaskService;

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
	public String startTask(@RequestBody TaskDto taskDto) {
		System.out.println("Inside startTask() :::::: Task2Controller ");
		
		try {
			String workflowId = taskDto.getWorkflowId();
			String taskId = taskDto.getTaskId();
			Map<String, String> attributes = taskDto.getAttributes();
			
			System.out.println("Parameters: workflowId="+workflowId +", taskId="+taskId +", attributes="+attributes);
			
			if(workflowId != null && workflowId.length() > 0 && taskId != null && taskId.length() > 0 && attributes != null && attributes.size() > 0) {
				
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
			e.printStackTrace();
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
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("email", "karthikcm.77@gmail.com");
		
		ExecuteTaskService emailService = new ExecuteTaskService(javaMailSender, "123", "1", attributes);
		new Thread(emailService).start();
		
		return "Triggered email.. check inbox!";
	}
	
}
