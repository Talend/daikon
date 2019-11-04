package org.talend.daikon;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.finders.GitItemFinder;
import org.talend.daikon.finders.ItemFinder;
import org.talend.daikon.finders.JiraItemFinder;
import org.talend.daikon.finders.MiscGitItemFinder;
import org.talend.daikon.model.ReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItemType;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

/**
 * Goal which generates release notes based on fixed Jira issues in current version.
 */
@SuppressWarnings("UnstableApiUsage")
@Mojo(name = "release-notes", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ReleaseNotes extends AbstractMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotes.class);

    @Parameter(defaultValue = "TDKN", property = "project", required = true)
    private String project;

    @Parameter(defaultValue = "${project.version}", property = "version")
    private String version;

    @Parameter(property = "user", required = true)
    private String user;

    @Parameter(property = "password", required = true)
    private String password;

    @Parameter(defaultValue = "${project.build.directory}", property = "output")
    private File output;

    @Parameter(defaultValue = "https://jira.talendforge.org", property = "server")
    private String server;

    @Parameter(defaultValue = "Daikon", property = "name", required = false)
    private String name;

    public void execute() throws MojoExecutionException {
        try {
            // Prepare output resources
            output.mkdirs();
            final File file = new File(output, version + ".adoc");
            LOGGER.debug("output file: {} ", file.getAbsolutePath());
            file.createNewFile();

            // Create Jira client
            final URI jiraServerUri = new URI(server);
            final String jiraVersion = StringUtils.substringBefore(version, "-");
            LOGGER.debug("Jira version: {}", jiraVersion);
            LOGGER.info("Connecting using '{}' / '{}'", user, StringUtils.isEmpty(password) ? "<empty>" : "****");
            final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            final JiraRestClient client = factory.createWithBasicHttpAuthentication(jiraServerUri, user, password);

            // Stream all release note items
            final Optional<Stream<ReleaseNoteItem>> streams = Stream.of( //
                    new JiraItemFinder(project, jiraVersion, server, client), //
                    new GitItemFinder(server, client), //
                    new MiscGitItemFinder() //
            ) //
                    .map(ItemFinder::find) //
                    .reduce(Stream::concat);

            // Create Ascii doc output
            final Stream<ReleaseNoteItem> issueStream = streams.get().distinct();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("= " + name + " Release Notes (" + jiraVersion + ")");

                ThreadLocal<ReleaseNoteItemType> previousIssueType = new ThreadLocal<>();
                issueStream //
                        .sorted(Comparator.comparingInt(i -> i.getIssueType().hashCode())) //
                        .forEach(i -> {
                            if (previousIssueType.get() == null || !previousIssueType.get().equals(i.getIssueType())) {
                                writer.println();
                                writer.println("== " + i.getIssueType().getDisplayName());
                                previousIssueType.set(i.getIssueType());
                            }
                            i.writeTo(writer);
                        });
            }
            LOGGER.info("Release notes generated @ '{}'.", file.getAbsoluteFile().getAbsolutePath());
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
