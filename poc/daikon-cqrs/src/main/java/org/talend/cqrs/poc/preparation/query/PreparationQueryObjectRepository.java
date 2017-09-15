package org.talend.cqrs.poc.preparation.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreparationQueryObjectRepository extends JpaRepository<PreparationQueryObject, String> {

}
