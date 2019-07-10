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
package org.talend.logging.audit.doc

import org.talend.logging.audit.impl.http.HttpEventSender

import static java.beans.Introspector.decapitalize

Closure generateConfig = { clazz ->
    def defaultInstance = clazz.getConstructor().newInstance()
    [clazz.methods]
        .flatten()
        .findAll { it.name.startsWith('set') && it.parameterCount == 1 } // settable config for log4j1/logback
        .collect { decapitalize(it.name.substring('set'.length())) } // config name (match field name)
        .sort()
        .findAll { clazz.getDeclaredField(it).isAnnotationPresent(HttpEventSender.Configuration.class) } // find the field to get the config
        .collect { // create the doc line
            def field = clazz.getDeclaredField(it)
            field.accessible = true
            "|`${it}`|${field.getAnnotation(HttpEventSender.Configuration.class).value()}|`${field.get(defaultInstance)}`"
        }
        .join('\n')
}

Closure updateConfig = { readme, clazz ->
    String className = clazz.name
    String startMarker = "// ${className}:documentation:start"
    String endMarker = "// ${className}:documentation:end"
    int start = readme.indexOf(startMarker)
    int end = readme.indexOf(endMarker)
    if (start < 0 || end < start) {
        throw new IllegalArgumentException("No marker for ${className}")
    }
"""
${readme.substring(0, start + startMarker.length())}

[opts="header",cols="a,a,a"]
|====
| Property | Description | Default Value
${generateConfig(clazz)}
|====

${readme.substring(end)}
""".trim() + '\n'
}

Closure doUpdate = { file, type ->
    file.text = updateConfig(file.text, HttpEventSender.class)
}

doUpdate(project.basedir.toPath().resolve('../README.adoc'), HttpEventSender.class)
