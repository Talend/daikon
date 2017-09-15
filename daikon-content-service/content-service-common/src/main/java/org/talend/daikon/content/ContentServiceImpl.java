package org.talend.daikon.content;

import static org.talend.daikon.content.ContentErrorCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.services.ContentService;

@ServiceImplementation
@ConditionalOnBean(ResourceResolver.class)
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ResourceResolver deletableResourceLoader;

    private DeletableResource getResource(@PathVariable("location") String location) {
        return deletableResourceLoader.getResource(location);
    }

    @Override
    public OutputStream get(@PathVariable("location") String location) {
        try {
            return getResource(location).getOutputStream();
        } catch (IOException e) {
            throw new TalendRuntimeException(UNABLE_TO_GET_CONTENT, e);
        }
    }

    @Override
    public InputStream put(@RequestParam("location") String location) {
        try {
            return getResource(location).getInputStream();
        } catch (IOException e) {
            throw new TalendRuntimeException(UNABLE_TO_PUT_CONTENT, e);
        }
    }

    @Override
    public void delete(@PathVariable("location") String location) {
        try {
            getResource(location).delete();
        } catch (IOException e) {
            throw new TalendRuntimeException(UNABLE_TO_DELETE_CONTENT, e);
        }
    }

    @Override
    public void clear() {
        try {
            deletableResourceLoader.clear("/**");
        } catch (IOException e) {
            throw new TalendRuntimeException(UNABLE_TO_CLEAR_CONTENT, e);
        }
    }
}
