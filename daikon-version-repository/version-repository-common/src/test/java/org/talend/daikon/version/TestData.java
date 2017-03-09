package org.talend.daikon.version;

import org.talend.daikon.version.api.Identifiable;

public class TestData implements Identifiable {

    private final String id;

    private String data;

    public TestData(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    public String getData() {
        return data;
    }

    public TestData setData(String data) {
        TestData modified = new TestData(id);
        modified.data = data;
        return modified;
    }

    public TestData copy() {
        TestData copy = new TestData(id);
        copy.data = this.data;
        return copy;
    }
}
