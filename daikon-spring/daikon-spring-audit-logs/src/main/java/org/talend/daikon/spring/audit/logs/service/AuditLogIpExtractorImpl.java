package org.talend.daikon.spring.audit.logs.service;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.talend.daikon.spring.audit.logs.config.AuditProperties;

public class AuditLogIpExtractorImpl implements AuditLogIpExtractor {

    private static final String INTERNAL_PROXIES = "" + //
            "10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" + //
            "192\\.168\\.\\d{1,3}\\.\\d{1,3}|" + //
            "169\\.254\\.\\d{1,3}\\.\\d{1,3}|" + //
            "127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" + //
            "172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|" + //
            "172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|" + //
            "172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|" + //
            "0:0:0:0:0:0:0:1|::1"; //

    private static final Pattern INTERNAL_PROXIES_PATTERN = Pattern.compile(INTERNAL_PROXIES);

    private static final String REMOTE_IP_HEADER = "X-Forwarded-For";

    private String trustedProxies;

    // This pattern matches nothing
    private Pattern trustedProxiesPattern = Pattern.compile("a^");

    public AuditLogIpExtractorImpl(AuditProperties auditProperties) {
        this.trustedProxies = auditProperties.getTrustedProxies();
        if (this.trustedProxies != null) {
            trustedProxiesPattern = Pattern.compile(trustedProxies);
        }
    }

    public String extract(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getHeader(REMOTE_IP_HEADER)).orElse(request.getRemoteAddr()).split(",")) //
                .map(String::trim)
                // Validate Ip format
                .filter(ip -> InetAddressValidator.getInstance().isValid(ip))
                // Do not keep ips matching internal proxy ips
                .filter(ip -> !INTERNAL_PROXIES_PATTERN.matcher(ip).matches())
                // Do not keep ips matching trusted proxy pattern
                .filter(ip -> !trustedProxiesPattern.matcher(ip).matches()) //
                .distinct() //
                .collect(Collectors.joining(", "));
    }
}
