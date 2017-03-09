package org.talend.daikon.client.configuration;

import java.util.List;
import java.util.stream.Collectors;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.daikon.client.Access;
import org.talend.daikon.client.ServiceFinder;

/**
 * This class groups all available {@link ServiceFinder} implementations.
 */
@Configuration
public class ClientServiceConfiguration {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired(required = false)
    private List<ServiceFinder> finders;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new BasicAuthRequestInterceptor("test", "test");
    }

    /**
     * Returns a list of {@link ServiceFinder} implementations that allow given <code>access</code>.
     * 
     * @param access An {@link Access access} type.
     * @return All {@link ServiceFinder} that allow access type, empty list if none found.
     * @see ServiceFinder#allow(Access)
     */
    public List<ServiceFinder> getFinders(Access access) {
        return finders.stream().filter(f -> f.allow(access)).collect(Collectors.toList());
    }

}
