package org.talend.daikon.statistic.pojo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HistogramUnit<T> {

    private int occurrences;

    private T lowerBound;

    private T upperBound;

    public HistogramUnit() {
        // for jackson
    }

    public HistogramUnit(int occurrences, T lowerBound, T upperBound) {
        this.occurrences = occurrences;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public T getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(T lowerBound) {
        this.lowerBound = lowerBound;
    }

    public T getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(T upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        HistogramUnit<?> that = (HistogramUnit<?>) o;

        return new EqualsBuilder().append(occurrences, that.occurrences).append(lowerBound, that.lowerBound)
                .append(upperBound, that.upperBound).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(occurrences).append(lowerBound).append(upperBound).toHashCode();
    }
}
