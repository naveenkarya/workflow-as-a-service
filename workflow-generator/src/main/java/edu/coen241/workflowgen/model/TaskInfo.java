package edu.coen241.workflowgen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskInfo {
    @Id
    private String id;
    private String taskName;
    private String serviceName;
    private String dockerImage;
    private String cpuLimit;
    private String memoryLimit;

}