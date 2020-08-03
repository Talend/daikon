package org.talend.daikon.spring.audit.logs.service;

import javax.servlet.http.HttpServletRequest;

public interface AuditLogIpExtractor {

    String extract(HttpServletRequest servletRequest);
}
