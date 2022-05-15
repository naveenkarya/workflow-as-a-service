package com.coen241.schedulerservice.services;

import com.coen241.schedulerservice.model.WorkflowSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WorkflowSpecService {
    @Autowired
    private RestTemplate restTemplate;
    private static String backendUrl = "https://3bd9eb30-a041-4fa7-90c7-9f2b6f92a960.mock.pstmn.io";
    public WorkflowSpec getWorkflowSpec(String workflowSpecId) {
        return restTemplate.getForObject(backendUrl + "/workflowSpec/" + workflowSpecId, WorkflowSpec.class);
    }

}
