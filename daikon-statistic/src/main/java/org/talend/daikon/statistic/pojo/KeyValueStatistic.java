package org.talend.daikon.statistic.pojo;

import java.util.Map;

public class KeyValueStatistic<T> extends Statistic<T> {

    private Map<String, T> value;

    public Map<String, T> getValue() {
        return value;
    }

    public void setValue(Map<String, T> value) {
        this.value = value;
    }
}
