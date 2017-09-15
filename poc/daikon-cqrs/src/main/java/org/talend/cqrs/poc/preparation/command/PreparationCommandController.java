package org.talend.cqrs.poc.preparation.command;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreateCommand;
import org.talend.cqrs.poc.preparation.command.steps.StepAddCommand;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdateCommand;

@RestController
public class PreparationCommandController {

    private CommandGateway commandGateway;

    public PreparationCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public CompletableFuture<String> createPreparation(@RequestParam(name = "name") String name,
            @RequestParam(name = "desc", defaultValue = "") String desc) {
        String randomUUID = UUID.randomUUID().toString();
        return commandGateway.send(new PreparationCreateCommand(randomUUID, name, desc));
    }
    

    @PostMapping("/step/{id}")
    public CompletableFuture<String> addStep(@PathVariable String id, @RequestParam(name = "type") String stepType) {
        return commandGateway.send(new StepAddCommand(id, stepType));
    }
    
    @PostMapping("/{id}")
    public CompletableFuture<String> updatePreparation(@PathVariable String id, @RequestParam(name = "name") String name,
            @RequestParam(name = "desc", defaultValue = "") String desc) {
        return commandGateway.send(new PreparationUpdateCommand(id, name, desc));
    }

}
