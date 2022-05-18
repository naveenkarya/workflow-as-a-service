package edu.coen241.workflowgen.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeploymentConfiguration {

    private static final int DEFAULT_SERVICE_PORT = 8080;
    public static final String NODE_PORT = "NodePort";
    public static final String CLUSTER_IP = "ClusterIP";
    private String serviceName;
    private String deploymentName;
    private String dockerImage;
    private String selector;
    private int port;
    private String serviceType;
    private Integer nodePort;
    public static DeploymentConfiguration SCHEDULER_CONFIG = buildSchedulerConfig();
    public static DeploymentConfiguration WORKFLOW_UI_CONFIG = buildWorkflowUIConfig();

    private static DeploymentConfiguration buildWorkflowUIConfig() {

        System.out.println("workflow-ui env: " + System.getenv().get("workflow-ui-docker-image"));
        return DeploymentConfiguration.builder()
                .dockerImage(System.getenv().getOrDefault("workflow-ui-docker-image", "nevin160/workflow-ui"))
                .serviceName("workflow-ui-service")
                .selector("workflow-ui")
                .deploymentName("workflow-ui-deployment")
                .serviceType(NODE_PORT)
                .nodePort(30000)
                .port(DEFAULT_SERVICE_PORT)
                .build();
    }

    private static DeploymentConfiguration buildSchedulerConfig() {
        System.out.println("scheduler env: " + System.getenv().get("scheduler-service-docker-image"));
        return DeploymentConfiguration.builder()
                .dockerImage(System.getenv().getOrDefault("scheduler-service-docker-image", "nevin160/scheduler-service"))
                .serviceName("scheduler-service")
                .selector("scheduler-service")
                .deploymentName("scheduler-deployment")
                .serviceType(CLUSTER_IP)
                .port(DEFAULT_SERVICE_PORT)
                .build();
    }

    public static DeploymentConfiguration fromTaskInfo(TaskInfo taskInfo) {
        DeploymentConfiguration deploymentConfiguration = DeploymentConfiguration.builder()
                .dockerImage(taskInfo.getDockerImage())
                .selector(taskInfo.getServiceName())
                .serviceName(taskInfo.getServiceName() + "-service")
                .deploymentName(taskInfo.getServiceName() + "-deployment")
                .port(DEFAULT_SERVICE_PORT)
                .nodePort(taskInfo.getNodePort())
                .build();
        if (taskInfo.getNodePort() != null) {
            deploymentConfiguration.setServiceType(NODE_PORT);
        } else {
            deploymentConfiguration.setServiceType(CLUSTER_IP);
        }
        return deploymentConfiguration;
    }
}
