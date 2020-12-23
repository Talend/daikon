package org.talend.daikon.logging.ecs;

<#assign className = output.file.name?keep_before(".")>
<#assign fields = model.content>

public final class ${className} {

<#list fields?keys as key>
    /**
    * ${fields[key].short}
    * Type: ${fields[key].type}
    */
    public static final String ${fields[key].dashed_name?replace('-', '_')?upper_case} = "${key}";
</#list>

}
