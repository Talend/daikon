package org.talend.daikon.content.local;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.talend.daikon.content.ContextualPatternResolver;

public class LocalResourceResolverTest {

    private LocalResourceResolver resolver;

    private Path resolverRepositoryPath;

    @Before
    public void createCache() throws IOException {
        final FileSystemResourceLoader resourceLoader;
        resourceLoader = new FileSystemResourceLoader();
        final PathMatchingResourcePatternResolver delegate = new PathMatchingResourcePatternResolver(resourceLoader);

        resolverRepositoryPath = Files.createTempDirectory(LocalResourceResolverTest.class.getSimpleName());
        resolver = new LocalResourceResolver(new ContextualPatternResolver(delegate, "target/store"), "target/store");
    }

    @After
    public void cleanupRepository() {
        FileUtils.deleteQuietly(resolverRepositoryPath.toFile());
    }

    @Test
    public void readWrite() throws IOException {
        String data = "toto data";
        String location = "toto location";

        // write
        try (OutputStream outputStream = resolver.getResource(location).getOutputStream();) {
            IOUtils.write(data, outputStream, UTF_8);
        }

        // read
        String contentRead;
        try (InputStream inputStream = resolver.getResource(location).getInputStream()) {
            contentRead = String.join("", IOUtils.readLines(inputStream, UTF_8));
        }

        // verify
        assertEquals(data, contentRead);
    }

    @Test
    public void readWrite_noClose() throws IOException {
        String data = "toto data";
        String location = "toto location";

        // write
        OutputStream outputStream = resolver.getResource(location).getOutputStream();
        IOUtils.write(data, outputStream, UTF_8);

        // read
        InputStream inputStream = resolver.getResource(location).getInputStream();
        String contentRead = String.join("", IOUtils.readLines(inputStream, UTF_8));

        // verify
        assertEquals(data, contentRead);
    }

    @Test
    @Ignore("There is a bug which makes implementation find two resources when there is one but containing a '/'")
    public void findResources() throws IOException {
        String data = "toto data";
        String location = "toto/location";

        // write
        OutputStream outputStream = resolver.getResource(location).getOutputStream();
        IOUtils.write(data, outputStream, UTF_8);

        // read
        assertEquals(1, Stream.of(resolver.getResources("**")).filter(Resource::isFile).count());
    }

}
