package edu.coen241.workflowgen.controller;

import edu.coen241.workflowgen.mapper.WorkflowResponseMapper;
import edu.coen241.workflowgen.model.WorkflowSpecInfo;
import edu.coen241.workflowgen.model.WorkflowSpecResponse;
import edu.coen241.workflowgen.repository.TaskInfoRepository;
import edu.coen241.workflowgen.repository.WorkflowSpecRepository;
import edu.coen241.workflowgen.service.TaskService;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class WorkflowRestController {
    public static final String DEFAULT_NAMESPACE = "default";
    @Autowired
    TaskInfoRepository taskInfoRepository;
    @Autowired
    WorkflowSpecRepository workflowSpecRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    WorkflowResponseMapper workflowResponseMapper;

    @PostMapping("/workflowSpec/create")
    public ResponseEntity<Map<String, String>> createWorkflowSpec(@RequestBody WorkflowSpecInfo workflowSpecInfo) {
        workflowSpecRepository.save(workflowSpecInfo);
        Map<String, String> result = new HashMap<>();
        result.put("workflowSpecId", workflowSpecInfo.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflowSpec")
    public ResponseEntity<List<WorkflowSpecInfo>> getAllWorkflowSpec() {
        return ResponseEntity.ok(workflowSpecRepository.findAll());
    }

    @GetMapping("/workflowSpec/{workflowSpecId}")
    public ResponseEntity<WorkflowSpecResponse> getWorkflowSpec(@PathVariable String workflowSpecId) {
        Optional<WorkflowSpecInfo> workflowSpecOp = workflowSpecRepository.findById(workflowSpecId);
        if (workflowSpecOp.isPresent()) {
            return ResponseEntity.ok(workflowResponseMapper.mapToWorkflowResponse(workflowSpecOp.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/workflowSpec")
    public ResponseEntity<Void> deleteAllWorkflowSpecs() {
        workflowSpecRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/workflowSpec/{workflowSpecId}/deploy")
    public ResponseEntity<Void> deployWorkflowSpec(@PathVariable String workflowSpecId) {
        //Optional<WorkflowSpecInfo> workflowSpecOp = workflowSpecRepository.findById(workflowSpecId);
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            createDeployment(client);
            createService(client);
        }
        catch (Exception e) {
            // TODO: handle failed
        }
        return ResponseEntity.ok().build();
    }

    private void createDeployment(KubernetesClient client) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName("workflow-ui-deployment")
                    .addToLabels("app", "workflow-ui")
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector()
                        .addToMatchLabels("app", "workflow-ui")
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", "workflow-ui")
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("workflow-ui")
                                .withImage("nevin160/workflow-ui")
                                .addNewPort()
                                    .withContainerPort(8080)
                                .endPort()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();
        client.apps().deployments().inNamespace("default").createOrReplace(deployment);
    }

    private void createService(KubernetesClient client) {
        Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName("workflow-ui-service")
                    .addToLabels("app", "workflow-ui-service")
                .endMetadata()
                .withNewSpec()
                    .withType("NodePort")
                    .addNewPort()
                        .withPort(8080)
                        .withNodePort(30000)
                    .endPort()
                    .addToSelector("app", "workflow-ui")
                .endSpec()
                .build();

        service = client.services().inNamespace(DEFAULT_NAMESPACE).create(service);

    }

}
