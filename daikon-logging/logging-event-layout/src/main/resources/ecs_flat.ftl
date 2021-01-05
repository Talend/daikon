package org.talend.daikon.logging.ecs;

<#assign className = output.file.name?keep_before(".")>
<#assign fields = model.content>

/**
* Enumeration of all ECS fields based on ecs_flat.yaml file
*/
public enum ${className} {
<#list fields?keys as key>
    /**
    * ${fields[key].short}
    * Type: ${fields[key].type}
    <#if fields[key].example??>
        <#if fields[key].example?is_date>
    * Example: ${fields[key].example?datetime_if_unknown?iso_utc}
        <#elseif fields[key].example?is_number>
    * Example: ${fields[key].example?c}
        <#else>
    * Example: ${fields[key].example}
        </#if>
    </#if>
    */
    ${fields[key].dashed_name?replace('-', '_')?upper_case}("${key}")<#if key?is_last>;<#else>,</#if>
</#list>

    public final String fieldName;

    private ${className}(String fieldName) {
        this.fieldName = fieldName;
    }
}
