package org.talend.daikon.content.journal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

/**
 * <p>
 * An implementation of {@link DeletableResource} that do not perform any lookup information on external systems to be
 * created.
 * </p>
 * <p>
 * When information from actual {@link DeletableResource} is required a call to {@link #materialize()} is performed to
 * retrieve information from {@link ResourceResolver}.
 * </p>
 * 
 * @see ResourceResolver#getResource(String)
 */
class LazyDeletableResource implements DeletableResource {

    private final String location;

    private final ResourceResolver resourceResolver;

    private DeletableResource resource;

    public LazyDeletableResource(String location, ResourceResolver resourceResolver) {
        this.location = location;
        this.resourceResolver = resourceResolver;
    }

    private synchronized DeletableResource materialize() {
        if (resource == null) {
            resource = resourceResolver.getResource(location);
        }
        return resource;
    }

    @Override
    @Nullable
    public String getFilename() {
        return location;
    }

    @Override
    public void delete() throws IOException {
        materialize().delete();
    }

    @Override
    public void move(String location) throws IOException {
        materialize().move(location);
    }

    @Override
    public String getAbsolutePath() throws IOException {
        return materialize().getAbsolutePath();
    }

    @Override
    public boolean isWritable() {
        return materialize().isWritable();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return materialize().getOutputStream();
    }

    @Override
    public WritableByteChannel writableChannel() throws IOException {
        return materialize().writableChannel();
    }

    @Override
    public boolean exists() {
        return materialize().exists();
    }

    @Override
    public boolean isReadable() {
        return materialize().isReadable();
    }

    @Override
    public boolean isOpen() {
        return materialize().isOpen();
    }

    @Override
    public boolean isFile() {
        return materialize().isFile();
    }

    @Override
    public URL getURL() throws IOException {
        return materialize().getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return materialize().getURI();
    }

    @Override
    public File getFile() throws IOException {
        return materialize().getFile();
    }

    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return materialize().readableChannel();
    }

    @Override
    public long contentLength() throws IOException {
        return materialize().contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return materialize().lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return materialize().createRelative(relativePath);
    }

    @Override
    public String getDescription() {
        return materialize().getDescription();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return materialize().getInputStream();
    }
}
