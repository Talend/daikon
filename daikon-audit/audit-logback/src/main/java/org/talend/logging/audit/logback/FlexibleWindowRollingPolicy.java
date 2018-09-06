package org.talend.logging.audit.logback;

import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;

public class FlexibleWindowRollingPolicy extends FixedWindowRollingPolicy {

    private int maxBackup;

    public int getMaxBackup() {
        return maxBackup;
    }

    public void setMaxBackup(int maxBackup) {
        this.maxBackup = maxBackup;
        setMaxIndex(maxBackup);
    }

    @Override
    protected int getMaxWindowSize() {
        return maxBackup;
    }
}
