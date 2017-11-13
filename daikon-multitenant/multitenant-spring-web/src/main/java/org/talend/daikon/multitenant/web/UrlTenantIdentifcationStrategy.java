package org.talend.daikon.multitenant.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

/**
 * A {@link TenantIdentificationStrategy strategy} which matches a request URI against a provided regular expression.
 * The first group of the expression will be used as the identification.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
public class UrlTenantIdentifcationStrategy implements TenantIdentificationStrategy {

    private Pattern pattern;

    @Override
    public Object identifyTenant(HttpServletRequest request) {
        Matcher matcher = pattern.matcher(request.getRequestURI());
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

    /**
     * Set the url pattern to use. This pattern should contain at least one group, the first of which will be used for
     * the tenant identity.
     * 
     * @param urlPattern
     */
    @Required
    public void setUrlPattern(String urlPattern) {
        this.pattern = Pattern.compile(urlPattern);
    }

}
