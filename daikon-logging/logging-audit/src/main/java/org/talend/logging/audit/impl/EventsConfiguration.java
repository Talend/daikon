package org.talend.logging.audit.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 *
 */
class EventsConfiguration {

    private EventsConfiguration() {
    }

    public static EventSet loadEvents(InputStream in) {
        Properties props = new Properties();
        EventSet events = new EventSet();

        try {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            boolean valueSet = false;

            final String[] elems = key.split(Pattern.quote("."));
            final String eventName = elems[0];
            final String eventProperty = elems[1];

            for (EventDefinition event : events) {
                if (event.getName().equals(eventName)) {
                    event.set(eventProperty, value);
                    valueSet = true;
                    break;
                }
            }

            if (!valueSet) {
                EventDefinition newEvent = new EventDefinition(eventName);
                newEvent.set(eventProperty, value);
                events.add(newEvent);
            }
        }

        return events;
    }
}
