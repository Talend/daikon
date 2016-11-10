package org.talend.daikon;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

/**
 * Unit tests run on a random port so can't be set in configuration. This configuration allows to create a server list
 * using the "local.server.port" environment variable.
 */
@Configuration
public class RandomServerPortFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomServerPortFinder.class);

    @Autowired
    private Environment environment;

    @Bean
    public ServerList<Server> serverList() {
        return new ServerList<Server>() {

            @Override
            public List<Server> getInitialListOfServers() {
                return Collections.emptyList();
            }

            @Override
            public List<Server> getUpdatedListOfServers() {
                return getServers();
            }

            private List<Server> getServers() {
                final String serverPortProperty = environment.getProperty("local.server.port");
                List<Server> serverList;
                if (serverPortProperty != null) {
                    serverList = Collections.singletonList(new Server("127.0.0.1", Integer.parseInt(serverPortProperty)));
                    LOGGER.info("Configure server list to: {}", serverList);
                } else {
                    serverList = Collections.emptyList();
                    LOGGER.warn("No server currently available.");
                }
                return serverList;
            }
        };
    }

}
