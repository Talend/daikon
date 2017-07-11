package org.talend.cqrs.poc.preparation.query;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PreparationQueryController {

    private PreparationQueryObjectRepository repository;

    public PreparationQueryController(PreparationQueryObjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PreparationQueryObject> findAllPreps() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public PreparationQueryObject findOnePrep(@PathVariable String id) {
        return repository.findOne(id);
    }

}
