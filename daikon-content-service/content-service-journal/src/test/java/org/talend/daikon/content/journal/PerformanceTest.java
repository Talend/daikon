package org.talend.daikon.content.journal;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = MOCK) // MOCK environment is important not to start tomcat (see TDKN-145)
@TestPropertySource(
        properties = { "content-service.store=local", "content-service.store.local.path=${java.io.tmpdir}/dataprep" })
/*
 * @TestPropertySource(properties = {
 * "content-service.store=s3",
 * "content-service.store.s3.authentication=TOKEN",
 * "content-service.store.s3.accessKey=",
 * "content-service.store.s3.secretKey=",
 * "content-service.store.s3.bucket=data-prep-francois",
 * "content-service.store.s3.region=eu-west-1"
 * })
 */
public class PerformanceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTest.class);

    @Autowired
    private ResourceResolver resolver;

    @Test
    public void performanceTest() throws IOException {
        final ResourceJournal resourceJournal = new ResourceJournal() {

            final Set<String> locations = new TreeSet<>();

            @Override
            public void sync() {
                // Do nothing
            }

            @Override
            public Stream<String> matches(String pattern) throws IOException {
                return locations.stream().filter(s -> s.startsWith(pattern.substring(0, pattern.lastIndexOf('*') - 1)));
            }

            @Override
            public void clear(String location) {
                locations.clear();
            }

            @Override
            public void add(String location) {
                locations.add(location);
            }

            @Override
            public void remove(String location) {
                locations.remove(location);
            }

            @Override
            public void move(String source, String target) {
                locations.remove(source);
                locations.add(target);
            }

            @Override
            public boolean exist(String location) {
                return locations.contains(location);
            }
        };
        resolver = new JournalizedResourceResolver(resolver, resourceJournal);

        LOGGER.info("Resolver implementation: " + resolver);

        System.out.println("Initial clean up...");
        resolver.clear("/test/**");
        System.out.println("Clean up done.");

        for (int i = 0; i < 11; i++) {
            System.out.println("----------------");
            long listStart = System.currentTimeMillis();
            final DeletableResource[] resources = resolver.getResources("/test/**");
            final long elapsedListTime = System.currentTimeMillis() - listStart;
            System.out.println("Resources found: " + resources.length);
            System.out.println("List time: " + elapsedListTime + " ms.");

            long addStart = System.currentTimeMillis();
            for (int j = 0; j < 100; j++) {
                final DeletableResource resource = resolver.getResource("/test/" + ((i * 100) + j) + ".txt");
                try (Writer out = new OutputStreamWriter(resource.getOutputStream())) {
                    out.write("this is more a test than an actual content");
                }
            }
            System.out.println("Add time: " + (System.currentTimeMillis() - addStart) + " ms.");
            System.out.println("----------------");
        }

        long clearStart = System.currentTimeMillis();
        resolver.clear("/test/**");
        System.out.println("Clear time: " + (System.currentTimeMillis() - clearStart) + " ms");
    }
}
