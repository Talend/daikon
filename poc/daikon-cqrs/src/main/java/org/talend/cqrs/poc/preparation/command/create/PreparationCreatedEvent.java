package org.talend.cqrs.poc.preparation.command.create;

public class PreparationCreatedEvent {

    // only getters here, it is read only.
    private String id;

    private String name;

    private String desc;

    public PreparationCreatedEvent(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
