package edu.coen241.workflowgen.model;

public enum DeploymentStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    DEPLOYED("Deployed"),
    FAILED("Failed");

    private String value;
    DeploymentStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
