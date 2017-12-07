package org.talend.logging.audit.impl;

class AuditConfigEntry {

    final AuditConfiguration entry;

    final Object value;

    final boolean alreadySet;

    AuditConfigEntry(AuditConfiguration entry, Object value, boolean alreadySet) {
        this.entry = entry;
        this.value = value;
        this.alreadySet = alreadySet;
    }
}
