package org.talend.daikon;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Comparator;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.BasicIssueType;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

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
            final URI jiraServerUri = new URI(server);
            final String jiraVersion = StringUtils.substringBefore(version, "-");
            LOGGER.debug("Jira version: {}", jiraVersion);

            // Create Jira client
            LOGGER.info("Connecting using '{}' / '{}'", user, StringUtils.isEmpty(password) ? "<empty>" : "****");
            final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

            final JiraRestClient client = factory.createWithBasicHttpAuthentication(jiraServerUri, user, password);
            final Promise<SearchResult> results = client //
                    .getSearchClient() //
                    .searchJql("project = '" + project + "' and fixVersion='" + jiraVersion + "' and status in (Closed, Done)");

            // Prepare output resources
            output.mkdirs();
            final File file = new File(output, version + ".adoc");
            LOGGER.debug("output file: {} ", file.getAbsolutePath());
            file.createNewFile();

            // Create Ascii doc output
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("= " + name + " Release Notes (" + jiraVersion + ")");

                ThreadLocal<BasicIssueType> previousIssueType = new ThreadLocal<>();
                StreamSupport.stream(results.claim().getIssues().spliterator(), false) //
                        .map(i -> {
                            final Promise<Issue> currentIssue = client.getIssueClient().getIssue(i.getKey());
                            return currentIssue.claim();
                        }) //
                        .sorted(Comparator.comparingInt(i -> i.getIssueType().hashCode())) //
                        .forEach(i -> {
                            if (previousIssueType.get() == null || !previousIssueType.get().equals(i.getIssueType())) {
                                writer.println();
                                writer.println("== " + i.getIssueType().getName());
                                previousIssueType.set(i.getIssueType());
                            }
                            writer.println("- link:" + jiraServerUri + "/browse/" + i.getKey() + "[" + i.getKey() + "]: "
                                    + i.getSummary());
                        });
            }
            LOGGER.info("Release notes generated @ '{}'.", file.getAbsoluteFile().getAbsolutePath());
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
