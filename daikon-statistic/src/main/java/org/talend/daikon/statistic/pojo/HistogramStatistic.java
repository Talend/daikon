package org.talend.daikon.statistic.pojo;

import java.io.Serializable;
import java.util.List;

public class HistogramStatistic<T extends Serializable> extends Statistic<T> {

    private String scaleUnit;

    private List<HistogramUnit<T>> value;

    public String getScaleUnit() {
        return scaleUnit;
    }

    public void setScaleUnit(String scaleUnit) {
        this.scaleUnit = scaleUnit;
    }

    public List<HistogramUnit<T>> getValue() {
        return value;
    }

    public void setValue(List<HistogramUnit<T>> value) {
        this.value = value;
    }

}