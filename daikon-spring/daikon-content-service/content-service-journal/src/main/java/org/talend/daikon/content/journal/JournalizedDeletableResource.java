package org.talend.daikon.content.journal;

import org.springframework.core.io.Resource;
import org.talend.daikon.content.DeletableResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

class JournalizedDeletableResource implements DeletableResource {

    private final DeletableResource resource;

    private final ResourceJournal resourceJournal;

    private String location;

    JournalizedDeletableResource(String location, DeletableResource resource, ResourceJournal resourceJournal) {
        this.location = location;
        this.resource = resource;
        this.resourceJournal = resourceJournal;
    }

    @Override
    public void delete() throws IOException {
        resource.delete();
        resourceJournal.remove(location);
    }

    @Override
    public void move(String location) throws IOException {
        resource.move(location);

        resourceJournal.move(this.location, location);
        this.location = location;
    }

    @Override
    public boolean isWritable() {
        return resource.isWritable();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return resource.getOutputStream();
    }

    @Override
    public boolean exists() {
        return resource.exists();
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
    public Resource createRelative(String s) throws IOException {
        return resource.createRelative(s);
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
}
