package org.talend.cqrs.poc.command;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.cqrs.poc.preparation.command.Preparation;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreateCommand;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreatedEvent;
import org.talend.cqrs.poc.preparation.command.steps.StepAddCommand;
import org.talend.cqrs.poc.preparation.command.steps.StepAddedEvent;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdateCommand;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdatedEvent;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PreparationCommandTest {

    private FixtureConfiguration<Preparation> fixture;

    @Before
    public void init() {
        fixture = new AggregateTestFixture<>(Preparation.class);
    }

    @Test
    public void testPreparationCreatedEvent() {
        fixture.givenNoPriorActivity().when(new PreparationCreateCommand("123", "my Prep", "desc"))
                .expectEvents(new PreparationCreatedEvent("123", "my Prep", "desc"));
    }

    @Test
    public void testPreparationStepAddedEvent() {
        fixture.given(new PreparationCreatedEvent("123", "my Prep", "desc"))
                .when(new StepAddCommand("123", "stepType")).expectEvents(new StepAddedEvent("123", "stepType"));
    }

    @Test
    public void testPreparationStepLimitRule() {
        fixture.given(new PreparationCreatedEvent("123", "my Prep", "desc"), new StepAddedEvent("123", "foo"), new StepAddedEvent("123", "foo"))
                .when(new StepAddCommand("123", "XstepType")).expectException(IllegalArgumentException.class);
    }

    @Test
    public void testPreparationUpdateEvent() {
        fixture.given(new PreparationCreatedEvent("123", "my Prep", "desc"))
                .when(new PreparationUpdateCommand("123", "my Prep", "desc"))
                .expectEvents(new PreparationUpdatedEvent("123", "my Prep", "desc"));
    }

}
