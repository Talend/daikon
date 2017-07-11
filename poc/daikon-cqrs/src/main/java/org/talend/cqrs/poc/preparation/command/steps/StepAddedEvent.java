package org.talend.cqrs.poc.preparation.command.steps;

public class StepAddedEvent {

    // only getters here, it is read only.
    private String id;

    private String stepType;

    public StepAddedEvent(String id, String stepType) {
        this.id = id;
        this.stepType = stepType;
    }

    public String getId() {
        return id;
    }

    public String getStepType() {
        return stepType;
    }

}
