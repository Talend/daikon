package org.talend.cqrs.poc.preparation.command;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreateCommand;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreatedEvent;
import org.talend.cqrs.poc.preparation.command.steps.StepAddCommand;
import org.talend.cqrs.poc.preparation.command.steps.StepAddedEvent;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdateCommand;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdatedEvent;

@Aggregate
public class Preparation {

    // the aggregate state is reconstructed by the framework using the history for a given @AggregateIdentifier using
    // event sourcing

    @AggregateIdentifier
    private String id;

    private int NbOfSteps;

    public Preparation() {
    }

    @CommandHandler
    public Preparation(PreparationCreateCommand preparationCC) {
        // check things
        Assert.hasLength(preparationCC.getName());
        // event sourcing here. Never change the state of the aggregator in commandhandlers, do so in Event handlers,
        // see @EvenSourcingHandler methods below.
        apply(new PreparationCreatedEvent(preparationCC.getId(), preparationCC.getName(), preparationCC.getDesc()));
    }

    @EventSourcingHandler
    public void on(PreparationCreatedEvent dce) {
        this.id = dce.getId();
    }

    @CommandHandler
    public void stepAdd(StepAddCommand stepAddC) {
        if (NbOfSteps >= 2) {
            throw new IllegalArgumentException();
        }
        apply(new StepAddedEvent(id, stepAddC.getStepType()));
    }

    @EventSourcingHandler
    public void on(StepAddedEvent sae) {
        NbOfSteps++;
    }

    @CommandHandler
    public void update(PreparationUpdateCommand preparationUC) {
        apply(new PreparationUpdatedEvent(id, preparationUC.getName(), preparationUC.getDesc()));
    }

}
