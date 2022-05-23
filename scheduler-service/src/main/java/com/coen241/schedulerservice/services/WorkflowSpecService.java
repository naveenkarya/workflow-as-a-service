package com.coen241.schedulerservice.services;

import com.coen241.schedulerservice.model.WorkflowSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WorkflowSpecService {
    @Autowired
    private RestTemplate restTemplate;
    private static String backendUrl = "http://workflow-spec-service:8080";
    public WorkflowSpec getWorkflowSpec(String workflowSpecId) {
        return restTemplate.getForObject(backendUrl + "/workflowSpec/" + workflowSpecId, WorkflowSpec.class);
    }

}
