package com.wfaas.task2.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wfaas.task2.services.ExecuteTaskService;
import com.wfaas.task2.services.StartTaskService;

@RestController
public class Task2Controller {
	
	private final static int NO_OF_THREADS = 10;
	
	
	@Autowired
	private StartTaskService startTaskService;
	
	
	
	private ExecutorService executorService;
	
	public Task2Controller() {
		System.out.println("Initialized the Fixed Thread Pool with "+NO_OF_THREADS);
		this.executorService = Executors.newFixedThreadPool(NO_OF_THREADS);
	}
	
	
	
	@RequestMapping(value = "/startTask", method = RequestMethod.POST)
	public String startTaskService(HttpServletRequest request) {
		
		// Get workflowId, taskId, Attributes from the request
		String workflowId = "";
		String taskId = "";
		JSONObject attributes = new JSONObject();
		
		
		// Call Execute and complete Task  - New thread
		ExecuteTaskService executeTaskService = new ExecuteTaskService(workflowId, taskId, attributes);
		this.executorService.submit(executeTaskService);
		
		
		
		// Send 200 - OK  (OR)  202 - Accepted
		return startTaskService.start();
	}
	
	
	
	/**** TEST PURPOSE ****/
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		String str = "Salaam, Rocky Bhai";
		return str;
	}
	
}
