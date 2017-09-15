package org.talend.cqrs.poc.preparation.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreatedEvent;
import org.talend.cqrs.poc.preparation.command.steps.StepAddedEvent;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdatedEvent;

/**
 * listen to all Preparation events and store/update the model in the local DB tuned to the query API.
 */
@Component
public class PreparationQueryObjectUpdater {

    private PreparationQueryObjectRepository repo;

    public PreparationQueryObjectUpdater(PreparationQueryObjectRepository repo) {
        this.repo = repo;
    }

    @EventHandler
    public void on(PreparationCreatedEvent dce) {
        PreparationQueryObject save = repo.save(new PreparationQueryObject(dce.getId(), dce.getName(), dce.getDesc()));
        System.out.println("saved:" + save);
    }

    @EventHandler
    public void on(StepAddedEvent sae) {
        PreparationQueryObject prep = repo.findOne(sae.getId());
        prep.getSteps().add(sae.getStepType());
        PreparationQueryObject save = repo.save(prep);
        System.out.println("added one step:" + save);
    }

    @EventHandler
    public void on(PreparationUpdatedEvent pue) {
        PreparationQueryObject save = repo.save(new PreparationQueryObject(pue.getId(), pue.getName(), pue.getDesc()));
        System.out.println("updated:" + save);
    }

}
