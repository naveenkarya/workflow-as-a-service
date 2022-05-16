package com.wfaas.task2.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import com.wfaas.task2.dto.TaskDto;

public class ExecuteTaskService implements Runnable {
	
	// Task: Send email notification
	
	private static final String EMAIL_SUBJECT = "Workflow as a Service email : Successfully registered";
	private static final String EMAIL_BODY = "You are receiving this email since you have just registered for the service!\n\n"
											+ "Thanks and Regards\n"
											+ "Workflow as a Services Team";
	

	
	@Value("${spring.mail.username}") private String sender;
	
	private JavaMailSenderImpl javaMailSender;
	private String workflowId;
	private String taskId;
	private Map<String, String> attributes;
	
	public ExecuteTaskService(JavaMailSenderImpl javaMailSender, String workflowId, String taskId, Map<String, String> attributes){
		this.javaMailSender = javaMailSender;
		this.workflowId = workflowId;
		this.taskId = taskId;
		this.attributes = attributes;
	}
	
	
	
	@Override
	public void run() {
		// Define and program your task here
		sendEmail();
	}
	
	
	// Task - Send email 
	private void sendEmail() {
		System.out.println("Inside sendEmail() :::::: TriggerEmailService ");
		System.out.println("workflowId="+workflowId +", taskId="+taskId +", attributes="+attributes);
		
		try {
			String recipient = this.attributes.containsKey("email") ? this.attributes.get("email") : null;
			System.out.println("Recipient email address "+recipient);
			
			if(recipient != null && recipient.length() > 0) {
				SimpleMailMessage mailMessage = new SimpleMailMessage();
				
				// set all the required attributes for the email
				mailMessage.setFrom(sender);
				mailMessage.setTo(recipient);
				mailMessage.setSubject(EMAIL_SUBJECT);
				mailMessage.setText(EMAIL_BODY);
				
				
				// trigger email
				javaMailSender.send(mailMessage);
				System.out.println("Success: Email sent successfully!");
				
				
				// send the response to Scheduler service
				sendResponseToScheduler(this.workflowId, this.taskId, this.attributes);
			}
			else {
				System.out.println("Error: Recipient email address cannot be empty!");
			}
		}
		catch(Exception e) {
			System.out.println("Some error occurred in sendEmail() ::::: TriggerEmailService, "+e);
			e.printStackTrace();
		}
	}
	
	
	public void sendResponseToScheduler(String workflowId, String taskId, Map<String, String> attributes) {
		System.out.println("Inside sendResponseToScheduler() :::::: ExecuteTaskService ");
		
		String URL = "http://scheduler-service:8080/completeTask"; // check for this URL
		System.out.println("URL to send POST request to Scheduler service: "+URL);
		
		try {
			/*HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, "application/json");*/
			
			
			TaskDto taskDto = new TaskDto(workflowId, taskId, attributes);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<TaskDto> requestEntity = new HttpEntity<TaskDto>(taskDto);
			
			// Send request (response) to Scheduler Service 
			ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
			
			String response = responseEntity.getBody();
			System.out.println("Response = "+response);
		}
		catch(Exception e) {
			System.out.println("Some error occurred in sendResponseToScheduler() ::::: TriggerEmailService, "+e);
			e.printStackTrace();
		}
	}
	
}
