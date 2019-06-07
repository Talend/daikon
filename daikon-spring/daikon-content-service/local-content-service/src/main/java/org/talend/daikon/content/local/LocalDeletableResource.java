package org.talend.daikon.content.local;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

public class LocalDeletableResource implements DeletableResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDeletableResource.class);

    private final WritableResource resource;

    private final ResourceResolver resolver;

    private boolean isDeleted;

    public LocalDeletableResource(ResourceResolver resolver, WritableResource resource) {
        this.resolver = resolver;
        this.resource = resource;
    }

    @Override
    public boolean isWritable() {
        return resource.isWritable();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!exists()) {
            FileUtils.touch(getFile());
        }
        return resource.getOutputStream();
    }

    @Override
    public boolean exists() {
        return !isDeleted && resource.exists();
    }

    @Override
    public boolean isReadable() {
        return resource.isReadable();
    }

    @Override
    public boolean isOpen() {
        return resource.isOpen();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return resource.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return new LocalDeletableResource(resolver, (WritableResource) resource.createRelative(relativePath));
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            FileUtils.touch(getFile());
        }
        return resource.getInputStream();
    }

    @Override
    public void delete() throws IOException {
        if (getFile().delete()) {
            isDeleted = true;
        }
    }

    @Override
    public void move(String location) throws IOException {
        final Path source = Paths.get(getURI());
        final Path target = Paths.get(resolver.getResource(location).getURI());
        try {
            Files.move(source, target, REPLACE_EXISTING, ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            LOGGER.debug("Atomic move not supported, trying without it.", e);
            Files.move(source, target, REPLACE_EXISTING);
        }
    }

}
