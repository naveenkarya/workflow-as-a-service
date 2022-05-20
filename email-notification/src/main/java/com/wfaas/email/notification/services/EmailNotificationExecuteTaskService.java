package com.wfaas.email.notification.services;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import com.wfaas.email.notification.common.TaskStatus;
import com.wfaas.email.notification.dto.TaskDto;

public class EmailNotificationExecuteTaskService implements Runnable {
	
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
	
	public EmailNotificationExecuteTaskService(JavaMailSenderImpl javaMailSender, String workflowId, String taskId, Map<String, String> attributes){
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
		System.out.println("\nInside sendEmail() :::::: EmailNotificationExecuteTaskService ");
		System.out.println("workflowId="+workflowId +", taskId="+taskId +", attributes="+attributes);
		
		try {
			String emailId = this.attributes.containsKey("email") ? this.attributes.get("email") : null;
			System.out.println("Recipient email address = "+emailId);
			
			if(emailId != null && emailId.length() > 0) {
				
				boolean isValidEmailId = isValidEmail(emailId);
				
				if(isValidEmailId) {
					SimpleMailMessage mailMessage = new SimpleMailMessage();
					
					// set all the required attributes for the email
					mailMessage.setFrom(sender);
					mailMessage.setTo(emailId);
					mailMessage.setSubject(EMAIL_SUBJECT);
					mailMessage.setText(EMAIL_BODY);
					
					// trigger email
					javaMailSender.send(mailMessage);
					System.out.println("Success: Email sent successfully!");
					
					
					// send the response as Task Completed to Scheduler service
					sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.COMPLETED);
				}
				else {
					System.out.println("Email Id is not in proper format! Please check!");
					sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.FAILED);
				}
			}
			else {
				System.out.println("Error: Email address cannot be empty!");
				sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.FAILED);
			}
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in sendEmail() ::::: EmailNotificationExecuteTaskService, "+e);
			e.printStackTrace();
			sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.FAILED);
		}
	}
	
	
	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                              
        Pattern pat = Pattern.compile(emailRegex);
        
        return pat.matcher(email).matches();
    }
	
	
	public void sendResponseToScheduler(String workflowId, String taskId, Map<String, String> attributes, String status) {
		System.out.println("\nInside sendResponseToScheduler() :::::: EmailNotificationExecuteTaskService ");
		
		String URL = "http://scheduler-service:8080/task/complete"; // Scheduler Service URL
		System.out.println("URL to send POST request to Scheduler service: "+URL);
		
		try {
			TaskDto taskDto = new TaskDto(workflowId, taskId, attributes, status);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<TaskDto> requestEntity = new HttpEntity<TaskDto>(taskDto);
			
			// Send response to Scheduler - Task is completed
			ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
			
			String response = responseEntity.getBody();
			System.out.println("Response = "+response);
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in sendResponseToScheduler() ::::: EmailNotificationExecuteTaskService, "+e);
			e.printStackTrace();
		}
	}
	
}
