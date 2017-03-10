package org.talend.daikon.content;

/**
 * A marker interface for Content service configuration classes. Detection code may then use
 * {@link org.springframework.boot.autoconfigure.condition.ConditionalOnBean} using this marker interface to detect if a content
 * service configuration is enabled.
 *
 * @see org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 */
public interface ContentServiceEnabled {
}
