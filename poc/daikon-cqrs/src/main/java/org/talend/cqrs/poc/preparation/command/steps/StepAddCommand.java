package org.talend.cqrs.poc.preparation.command.steps;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class StepAddCommand {

    @TargetAggregateIdentifier
    private String id;

    private String stepType;

    public StepAddCommand(String id, String stepType) {
        this.id = id;
        this.stepType = stepType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

}
