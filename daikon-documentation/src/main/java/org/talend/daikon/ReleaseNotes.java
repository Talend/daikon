package org.talend.daikon;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.finders.AuthorFinder;
import org.talend.daikon.finders.git.GitAuthorFinder;
import org.talend.daikon.finders.git.GitReleaseDateFinder;
import org.talend.daikon.finders.git.JiraGitItemFinder;
import org.talend.daikon.finders.ItemFinder;
import org.talend.daikon.finders.git.MiscGitItemFinder;
import org.talend.daikon.model.Author;
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

    @Parameter(defaultValue = "https://github.com/Talend/daikon", property = "github-repository")
    private String gitHubRepositoryUrl;

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
            final Optional<? extends Stream<? extends ReleaseNoteItem>> streams = Stream.<ItemFinder> of( //
                    new JiraGitItemFinder("../", server, client, jiraVersion, gitHubRepositoryUrl), //
                    new MiscGitItemFinder("../", jiraVersion, gitHubRepositoryUrl) //
            ) //
                    .map(ItemFinder::find) //
                    .reduce(Stream::concat);

            // Stream all authors for release
            final AuthorFinder gitAuthorFinder = new GitAuthorFinder(jiraVersion, "../", gitHubRepositoryUrl);
            Stream<Author> authors = gitAuthorFinder.findAuthors();

            // Find release date
            final GitReleaseDateFinder releaseDateFinder = new GitReleaseDateFinder(jiraVersion, "../", gitHubRepositoryUrl);
            final Date releaseDate = releaseDateFinder.find();

            // Create Ascii doc output
            final Stream<? extends ReleaseNoteItem> issueStream = streams.get().distinct();
            try (PrintWriter writer = new PrintWriter(file)) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
                writer.println("= " + name + " Release Notes (" + jiraVersion + ") - " + dateFormat.format(releaseDate));

                writer.println();
                final String allAuthors = authors.map(author -> "@" + author.getName()).collect(Collectors.joining(", "));
                writer.println("Thanks to " + allAuthors);

                final ThreadLocal<ReleaseNoteItemType> previousIssueType = new ThreadLocal<>();
                issueStream //
                        .peek(item -> getLog().info("Found item: " + item)) //
                        .sorted(Comparator.comparingInt(i -> i.getIssueType().ordinal())) //
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
