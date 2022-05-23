package edu.coen241.workflowgen.service;

import edu.coen241.workflowgen.model.DeploymentConfiguration;
import edu.coen241.workflowgen.model.DeploymentStatus;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
                                .withImagePullPolicy("Always")
                                .withNewResources()
                                .addToLimits(config.getResourceLimits())
                                .endResources()
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
        if(client.services().inNamespace(DEFAULT_NAMESPACE).withLabel("app", config.getServiceName()).list().getItems().size() > 0) {
            log.info("Service: {} already exists. Redeploying.", config.getServiceName());
            service = client.services().inNamespace(DEFAULT_NAMESPACE).replace(service);
        }
        else {
            log.info("Deploying: {}.", config.getServiceName());
            service = client.services().inNamespace(DEFAULT_NAMESPACE).create(service);
        }

        return service.getSpec().getPorts().get(0).getNodePort();

    }
    // @formatter:on

    public String getDeploymentStatus(String serviceName) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            DeploymentList depList = client.apps().deployments().inNamespace(DEFAULT_NAMESPACE).withLabel("app", serviceName + "-dep").list();
            if (!CollectionUtils.isEmpty(depList.getItems())) {
                io.fabric8.kubernetes.api.model.apps.DeploymentStatus status = depList.getItems().get(0).getStatus();
                if (status.getReadyReplicas() == status.getReplicas()) {
                    return DeploymentStatus.DEPLOYED.getValue();
                } else {
                    return client.pods().inNamespace(DEFAULT_NAMESPACE).withLabel("app", serviceName).list().getItems().get(0).getStatus().getPhase();
                }
            } else {
                return DeploymentStatus.NOT_STARTED.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DeploymentStatus.ERROR_CANNOT_GET.getValue();
    }
}
