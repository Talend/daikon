package org.talend.daikon.statistic.pojo;

public class SimpleStatistic<T> extends Statistic<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
