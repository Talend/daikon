package org.talend.daikon;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class ReleaseNotesTest {

    @Rule
    public MojoRule rule = new MojoRule();

    /**
     * @throws Exception if any
     */
    @Ignore("Requires actual credentials for test")
    @Test
    public void testSomething() throws Exception {
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        ReleaseNotes releaseNotes = (ReleaseNotes) rule.lookupConfiguredMojo(pom, "release-notes");
        assertNotNull(releaseNotes);
        releaseNotes.execute();
    }

}
