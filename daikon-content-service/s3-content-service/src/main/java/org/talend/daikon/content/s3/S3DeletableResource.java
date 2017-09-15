package org.talend.daikon.content.s3;

import static org.talend.daikon.content.s3.LocationUtils.toS3Location;
import static org.talend.daikon.content.s3.LocationUtils.S3PathBuilder.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.talend.daikon.content.DeletableResource;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

class S3DeletableResource implements DeletableResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3DeletableResource.class);

    private final WritableResource resource;

    private final AmazonS3 amazonS3;

    private final String location;

    private final String bucket;

    private final String root;

    private boolean isDeleted;

    protected S3DeletableResource(WritableResource resource, AmazonS3 amazonS3, String location, String bucket, String root) {
        this.resource = resource;
        this.amazonS3 = amazonS3;
        this.location = location;
        this.bucket = bucket;
        this.root = root;
    }

    @Override
    public void delete() throws IOException {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, location));
            isDeleted = true;
        } catch (AmazonClientException e) {
            throw new IOException("Unable to delete '" + location + "' in bucket '" + bucket + "'.", e);
        }
    }

    @Override
    public void move(String location) throws IOException {
        final String moveLocation = builder().append(root).append(toS3Location(location)).build().substring(1);
        final CopyObjectResult result = amazonS3.copyObject(new CopyObjectRequest(bucket, this.location, bucket, moveLocation));
        if (result == null) {
            LOGGER.error("Unable to move {} to {}", this.location, moveLocation);
        } else {
            LOGGER.info("Copied {} to {}, now deleting {}", this.location, moveLocation, this.location);
            delete();
        }
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
        final Resource relative = resource.createRelative(relativePath);
        if (relative instanceof WritableResource) {
            return new S3DeletableResource((WritableResource) relative, amazonS3, location, bucket, root);
        } else {
            return relative;
        }
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
        return resource.getInputStream();
    }

    @Override
    public boolean isWritable() {
        return resource.isWritable();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new CloseLenientOutputStream(resource.getOutputStream());
    }

    /**
     * An {@link OutputStream} implementation that prevents errors in case someone calls {@link #write(int)} <b>after</b> close
     * (seems to happen with Apache POI when writing Excel files).
     */
    private static class CloseLenientOutputStream extends OutputStream {

        private final OutputStream outputStream;

        boolean isClosed;

        private CloseLenientOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
            isClosed = false;
        }

        @Override
        public void write(int b) throws IOException {
            if (!isClosed) {
                outputStream.write(b);
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (!isClosed) {
                outputStream.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (!isClosed) {
                outputStream.write(b, off, len);
            }
        }

        @Override
        public void flush() throws IOException {
            if (!isClosed) {
                outputStream.flush();
            }
        }

        @Override
        public void close() throws IOException {
            try {
                outputStream.close();
            } finally {
                isClosed = true;
            }
        }
    }
}
