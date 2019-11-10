package org.talend.daikon.finders;

import org.talend.daikon.model.Author;

import java.util.stream.Stream;

public interface AuthorFinder {

    Stream<Author> findAuthors();
}
