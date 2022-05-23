package edu.coen241.workflowgen.model;

public enum DeploymentStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    DEPLOYED("Deployed"),
    FAILED("Failed"),
    ERROR_CANNOT_GET ("Cannot Retrieve Status");

    private String value;
    DeploymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
