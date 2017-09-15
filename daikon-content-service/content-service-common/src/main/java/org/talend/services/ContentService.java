package org.talend.services;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.talend.daikon.annotation.Service;
import org.talend.daikon.content.ResourceResolver;

/**
 * A service to manages raw content (using {@link InputStream in} and {@link OutputStream out} streams).
 */
@Service(name = "daikon.ContentService")
public interface ContentService {

    /**
     * Returns an {@link OutputStream} to the resource at given <code>location</code>.
     * @param location A non-null, non-empty location for the resource.
     * @return An {@link OutputStream} that can be used to write to resource at <code>location</code>.
     * @see ResourceResolver#getResource(String)
     */
    @RequestMapping(path = "/content/{location}", method = GET)
    OutputStream get(@PathVariable("location") String location);

    /**
     * Returns an {@link InputStream} to the resource at given <code>location</code>.
     * @param location A non-null, non-empty location for the resource.
     * @return An {@link InputStream} that can be used to read to resource at <code>location</code>.
     */
    @RequestMapping(path = "/content", method = POST)
    InputStream put(@RequestParam("location") String location);

    /**
     * Deletes the resource at given <code>location</code>.
     * @param location A non-null, non-empty location for the resource.
     */
    @RequestMapping(path = "/content/{location}", method = DELETE)
    void delete(@PathVariable("location") String location);

    /**
     * Removes all content in content service.
     */
    @RequestMapping(path = "/content", method = DELETE)
    void clear();
}
