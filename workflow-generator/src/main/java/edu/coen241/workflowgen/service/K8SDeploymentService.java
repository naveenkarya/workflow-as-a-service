package edu.coen241.workflowgen.service;

import edu.coen241.workflowgen.model.DeploymentConfiguration;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class K8SDeploymentService {

    public static final String DEFAULT_NAMESPACE = "default";

    public Integer deployService(DeploymentConfiguration deploymentConfiguration) {

        try (KubernetesClient client = new DefaultKubernetesClient()) {
            createDeployment(client, deploymentConfiguration);
            return createService(client, deploymentConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // @formatter:off
    private void createDeployment(KubernetesClient client, DeploymentConfiguration config) {
        log.info("Deploying: " + config.getDeploymentName());
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(config.getDeploymentName())
                    .addToLabels("app", config.getDeploymentName())
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector()
                        .addToMatchLabels("app", config.getSelector())
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", config.getSelector())
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(config.getSelector())
                                .withImage(config.getDockerImage())
                                .addNewPort()
                                    .withContainerPort(config.getPort())
                                .endPort()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();
        client.apps().deployments().inNamespace(DEFAULT_NAMESPACE).createOrReplace(deployment);
    }


    private Integer createService(KubernetesClient client, DeploymentConfiguration config) {
        log.info("Deploying: " + config.getServiceName());
        io.fabric8.kubernetes.api.model.Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(config.getServiceName())
                    .addToLabels("app", config.getServiceName())
                .endMetadata()
                .withNewSpec()
                    .withType(config.getServiceType())
                    .addNewPort()
                        .withPort(config.getPort())
                        .withNodePort(config.getNodePort())
                    .endPort()
                    .addToSelector("app", config.getSelector())
                .endSpec()
                .build();

        service = client.services().inNamespace(DEFAULT_NAMESPACE).createOrReplace(service);
        return service.getSpec().getPorts().get(0).getNodePort();

    }
    public void getDeploymentStatus(String serviceName) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            log.info("checking: " + client.pods().inNamespace("default").withName(serviceName).isReady());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // @formatter:on
}
