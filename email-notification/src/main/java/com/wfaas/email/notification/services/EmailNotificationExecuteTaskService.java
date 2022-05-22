package com.wfaas.email.notification.services;

import java.util.Arrays;
import java.util.List;
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
	
	// Task based Constants
	private static final String EMAIL = "email";

	private static final String MEAL_1 = "Pizza Meal";
	private static final String MEAL_2 = "Hamburger Meal";
	private static final String MEAL_3 = "Hot Dog Meal";
	private static final String MEAL_4 = "Milkshake";
	private static final List<String> ITEMS_LIST = Arrays.asList(MEAL_1, MEAL_2, MEAL_3, MEAL_4);
	private static final String TOTAL_BILL = "Total";
	
	private static final String TAB = "\t\t";
	private static final String NEW_LINE = "\n";

	
	@Value("${spring.mail.username}") private String senderEmailId;
	
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
		try {
			Thread.sleep(10000);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Define and program your task here
		sendEmail();
	}
	
	
	// Task - Send email notification
	private void sendEmail() {
		System.out.println("\nInside sendEmail() :::::: EmailNotificationExecuteTaskService ");
		System.out.println("workflowId="+workflowId +", taskId="+taskId +", attributes="+attributes);
		
		try {
			String recipientEmailId = this.attributes.containsKey(EMAIL) ? this.attributes.get(EMAIL) : null;
			System.out.println("Recipient email address = "+recipientEmailId);
			
			if(recipientEmailId != null && recipientEmailId.length() > 0) {
				
				// Validate recipient email id
				boolean isValidEmailId = isValidEmail(recipientEmailId);
				
				if(isValidEmailId) {
					// Compose email
					SimpleMailMessage mail = composeEmail(senderEmailId, recipientEmailId, this.attributes);					
					
					// trigger email
					javaMailSender.send(mail);
					System.out.println("Success: Email sent successfully!");
					
					
					// send the response as Task Completed to Scheduler service
					sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.COMPLETED, "Task is successfully completed");
				}
				else {
					String statusMessage = "Error: Email address entered is not in proper format! Please check!";
					sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.FAILED, statusMessage);
				}
			}
			else {
				String statusMessage = "Error: Email address cannot be empty! Please check!";
				sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.FAILED, statusMessage);
			}
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in sendEmail() ::::: EmailNotificationExecuteTaskService, "+e);
			e.printStackTrace();
			sendResponseToScheduler(this.workflowId, this.taskId, this.attributes, TaskStatus.FAILED, "Error: Some exception occurred while sending email. Please check logs for more information!");
		}
	}
	
	
	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                              
        Pattern pat = Pattern.compile(emailRegex);
        
        return pat.matcher(email).matches();
    }
	
	
	public SimpleMailMessage composeEmail(String fromEmailId, String toEmailId, Map<String, String> attributes) {
		SimpleMailMessage mail = new SimpleMailMessage();
		
		try {
			// set all the required attributes for the email
			mail.setFrom(fromEmailId);
			mail.setTo(toEmailId);
			
			String emailSubject = "Receipt detals for your food order ";
			mail.setSubject(emailSubject);
			
			String emailBody = 
				"Hello Customer,"+NEW_LINE +NEW_LINE
				+"Details of your food order is as follows: "+NEW_LINE +NEW_LINE
					+formOrderDetails(attributes)
					
					+"Thank you for ordering with us.. See you again!"+NEW_LINE+NEW_LINE
				
				+ "Thanks and Regards"+NEW_LINE
				+ "Restaurant Team";
			
			mail.setText(emailBody);
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in composeEmail() ::::: EmailNotificationExecuteTaskService, "+e);
			e.printStackTrace();
		}

		return mail;
	}
	
	
	public String formOrderDetails(Map<String, String> attributes) {
		String orderDetails = "Sl No."+TAB +"Item Name"+TAB +"Quantity"+TAB +"Price"+TAB +NEW_LINE;
		String seperator = "_";
		
		try{
			int index = 1;
			
			for(Map.Entry<String, String> order : attributes.entrySet()) {
				String item = order.getKey();
				String value = order.getValue();
				
				if(ITEMS_LIST.contains(item)) {
					String valueArr[] = value.split(seperator);
					String quantity = valueArr[0];
					String price = valueArr[1];
					
					if(index == 1 || index == 4) {
						quantity = "        " +quantity;
						price = "        " +price;
					}else if(index == 2) {
						quantity = "   " +quantity;
						price = "          " +price;
					}else if(index == 3) {
						quantity = "      " +quantity;
						price = "             " +price;
					}
					
					orderDetails += index+TAB +item+TAB +quantity+TAB +price+"$"+TAB +NEW_LINE;
					index++;
				}
			}
			
			orderDetails += "Total"+TAB +attributes.get(TOTAL_BILL)+"$" +NEW_LINE +NEW_LINE +NEW_LINE;
		}
		catch(Exception e) {
			System.out.println("\n\nSome error occurred in formOrderDetails() ::::: EmailNotificationExecuteTaskService, "+e);
			e.printStackTrace();
		}
		
		return orderDetails;
	}
	
	
	public void sendResponseToScheduler(String workflowId, String taskId, Map<String, String> attributes, String status, String statusMesage) {
		System.out.println("\nInside sendResponseToScheduler() :::::: EmailNotificationExecuteTaskService ");
		
		String URL = "http://scheduler-service:8080/task/complete"; // Scheduler Service URL
		System.out.println("URL to send POST request to Scheduler service: "+URL);
		
		try {
			TaskDto taskDto = new TaskDto(workflowId, taskId, attributes, status, statusMesage);
			
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
