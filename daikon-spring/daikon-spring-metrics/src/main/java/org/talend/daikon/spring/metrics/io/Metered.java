// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.spring.metrics.io;

public interface Metered {

    enum Type {
        IN("in"),
        OUT("out");

        private String tag;

        Type(String tag) {
            this.tag = tag;
        }

        String getMeterTag() {
            return tag;
        }

        public String getTag() {
            return tag;
        }
    }

    long getVolume();

    Type getType();

}
