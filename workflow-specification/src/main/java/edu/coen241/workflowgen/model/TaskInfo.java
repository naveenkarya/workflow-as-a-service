package edu.coen241.workflowgen.model;

import io.fabric8.kubernetes.api.model.Quantity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class TaskInfo {
    @Id
    private String id;
    private String taskName;
    private String serviceName;
    private String dockerImage;
    private String cpuLimit;
    private String memoryLimit;
    private Integer nodePort;

    @Transient
    public boolean isCpuLimitNotEmpty() {
        return cpuLimit != null && cpuLimit.trim().length() > 0;
    }

    @Transient
    public boolean isMemoryLimitNotEmpty() {
        return memoryLimit != null && memoryLimit.trim().length() > 0;
    }
    @Transient
    public boolean isCpuLimitValid() {
        if (isCpuLimitNotEmpty()) {
            try {
                Quantity.parse(cpuLimit);
            } catch (IllegalArgumentException ie) {
                log.error("cpu limit {} invalid.", cpuLimit);
                return false;
            }
        }
        return true;
    }
    @Transient
    public boolean isMemoryLimitValid() {
        if (isMemoryLimitNotEmpty()) {
            try {
                Quantity.parse(memoryLimit);
            } catch (IllegalArgumentException ie) {
                log.error("memory limit {} invalid.", memoryLimit);
                return false;
            }
        }
        return true;
    }

    public boolean isNodePortValid() {
        if(nodePort == null) return true;
        if(nodePort >= 30003 && nodePort <= 32767) return true;
        return false;
    }
}