package org.talend.daikon.content.s3;

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

/**
 * A {@link DeletableResource} implementation that can contains fixed information about host name and bucket path for
 * a S3 resource. Useful for Minio/Custom resources that rely on AWS client (allow to workaround missing AWS
 * information).
 */
public class FixedURLS3Resource implements DeletableResource {

    private final String host;

    private final String s3Location;

    private final DeletableResource delegate;

    public FixedURLS3Resource(String host, String s3Location, DeletableResource delegate) {
        this.host = host;
        this.s3Location = s3Location;
        this.delegate = delegate;
    }

    @Override
    public void delete() throws IOException {
        delegate.delete();
    }

    @Override
    public void move(String location) throws IOException {
        delegate.move(location);
    }

    @Override
    public boolean isWritable() {
        return delegate.isWritable();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return delegate.getOutputStream();
    }

    @Override
    public WritableByteChannel writableChannel() throws IOException {
        return delegate.writableChannel();
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }

    @Override
    public boolean isReadable() {
        return delegate.isReadable();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public boolean isFile() {
        return delegate.isFile();
    }

    @Override
    public URL getURL() throws IOException {
        if (s3Location.startsWith("/")) {
            return new URL(host + s3Location);
        } else {
            return new URL(host + "/" + s3Location);
        }
    }

    @Override
    public URI getURI() {
        if (s3Location.startsWith("/")) {
            return URI.create(host + s3Location);
        } else {
            return URI.create(host + "/" + s3Location);
        }
    }

    @Override
    public File getFile() throws IOException {
        return delegate.getFile();
    }

    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return delegate.readableChannel();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return delegate.lastModified();
    }

    @Override
    public Resource createRelative(String s) throws IOException {
        return delegate.createRelative(s);
    }

    @Override
    @Nullable
    public String getFilename() {
        return delegate.getFilename();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }
}
