package org.talend.daikon.spring.audit.logs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit")
public class AuditProperties {

    private String trustedProxies;

    public String getTrustedProxies() {
        return trustedProxies;
    }

    public AuditProperties setTrustedProxies(String trustedProxies) {
        this.trustedProxies = trustedProxies;
        return this;
    }
}
