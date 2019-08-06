/**
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.logging.audit.impl.http;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

public class HttpEventSenderTest {

    @Rule
    public final ProvideSystemProperty provideSystemProperty = new ProvideSystemProperty(
            "org.talend.logging.audit.impl.http.HttpEventSender.username", "system-prop-user") // String
                    .and("org.talend.logging.audit.impl.http.HttpEventSender.encoding", "UTF-16") // Charset
                    .and("org.talend.logging.audit.impl.http.HttpEventSender.connectTimeout", "5000"); // int

    @Test
    public void overrideSystemProps() {
        final HttpEventSender sender = new HttpEventSender();
        try {
            sender.start();
        } catch (final HttpAppenderException ex) {
            // not important, we went through overriding anyway
        }
        assertEquals("system-prop-user", sender.getUsername());
        assertEquals(5000, sender.getConnectTimeout());
        assertEquals(StandardCharsets.UTF_16, sender.getEncoding());
    }
}
