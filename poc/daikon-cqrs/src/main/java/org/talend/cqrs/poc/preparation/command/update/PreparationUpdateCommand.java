package org.talend.cqrs.poc.preparation.command.update;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class PreparationUpdateCommand {

    @TargetAggregateIdentifier
    private String id;

    private String name;

    private String desc;

    public PreparationUpdateCommand(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
