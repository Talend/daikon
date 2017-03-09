package org.talend.daikon.version.events;

import org.springframework.data.repository.CrudRepository;
import org.talend.daikon.version.api.DataEvent;

public interface DataEventStorage extends CrudRepository<DataEvent, String> {
}
