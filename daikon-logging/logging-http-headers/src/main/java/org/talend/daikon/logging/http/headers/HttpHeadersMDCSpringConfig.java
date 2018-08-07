package org.talend.daikon.logging.http.headers;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class HttpHeadersMDCSpringConfig {

    @Bean
    public FilterRegistrationBean httpHeadersMDCFilter() {
        FilterRegistrationBean answer = new FilterRegistrationBean();

        answer.setFilter(new HttpHeadersMDCFilter());
        answer.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER + 1);
        answer.setAsyncSupported(true);

        return answer;
    }
}
