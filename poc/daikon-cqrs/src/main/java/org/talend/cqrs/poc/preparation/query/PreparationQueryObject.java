package org.talend.cqrs.poc.preparation.query;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PreparationQueryObject {

    @Id
    private String id;

    private String name;

    private String description;

    @ElementCollection
    private List<String> steps;

    public PreparationQueryObject(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        // this.setTag(new TagImpl(id));
    }

    public PreparationQueryObject() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    // public Tag getTag() {
    // return tag;
    // }
    //
    // public void setTag(Tag tag) {
    // this.tag = tag;
    // }
}
