/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.daikon.sandbox;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.junit.Test;

public class MvnUrlParserTest {

    @Test(expected = MalformedURLException.class)
    public void constructorWithNullPath() throws MalformedURLException {
        new MvnUrlParser(null);
    }

    @Test(expected = MalformedURLException.class)
    public void urlStartingWithRepositorySeparator() throws MalformedURLException {
        new MvnUrlParser("!group");
    }

    @Test(expected = MalformedURLException.class)
    public void urlEndingWithRepositorySeparator() throws MalformedURLException {
        new MvnUrlParser("http://repository!");
    }

    @Test(expected = MalformedURLException.class)
    public void urlWithRepositoryAndNoGroup() throws MalformedURLException {
        new MvnUrlParser("http://repository!");
    }

    @Test(expected = MalformedURLException.class)
    public void urlWithoutRepositoryAndNoGroup() throws MalformedURLException {
        new MvnUrlParser("");
    }

    @Test(expected = MalformedURLException.class)
    public void urlWithRepositoryAndNoArtifact() throws MalformedURLException {
        new MvnUrlParser("http://repository!group");
    }

    @Test(expected = MalformedURLException.class)
    public void urlWithoutRepositoryAndNoArtifact() throws MalformedURLException {
        new MvnUrlParser("group");
    }

    @Test
    public void urlWithRepositoryAndGroupArtifact() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("http://repository@id=fake!group/artifact");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "LATEST", MvnUrlParser.getVersion());
        assertEquals("Type", "jar", MvnUrlParser.getType());
        assertEquals("Classifier", null, MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/LATEST/artifact-LATEST.jar", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithoutRepositoryAndGroupArtifact() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "LATEST", MvnUrlParser.getVersion());
        assertEquals("Type", "jar", MvnUrlParser.getType());
        assertEquals("Classifier", null, MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/LATEST/artifact-LATEST.jar", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithRepositoryAndGroupArtifactVersionType() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("http://repository@id=fake!group/artifact/version/type");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "version", MvnUrlParser.getVersion());
        assertEquals("Type", "type", MvnUrlParser.getType());
        assertEquals("Classifier", null, MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/version/artifact-version.type", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithRepositoryAndGroupArtifactVersionTypeClassifier() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("http://repository@id=fake!group/artifact/version/type/classifier");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "version", MvnUrlParser.getVersion());
        assertEquals("Type", "type", MvnUrlParser.getType());
        assertEquals("Classifier", "classifier", MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/version/artifact-version-classifier.type", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithoutRepositoryAndGroupArtifactVersionType() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version/type");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "version", MvnUrlParser.getVersion());
        assertEquals("Type", "type", MvnUrlParser.getType());
        assertEquals("Classifier", null, MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/version/artifact-version.type", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithoutRepositoryAndGroupArtifactVersionTypeClassifier() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version/type/classifier");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "version", MvnUrlParser.getVersion());
        assertEquals("Type", "type", MvnUrlParser.getType());
        assertEquals("Classifier", "classifier", MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/version/artifact-version-classifier.type", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithoutRepositoryAndGroupArtifactVersionClassifier() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version//classifier");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "version", MvnUrlParser.getVersion());
        assertEquals("Type", "jar", MvnUrlParser.getType());
        assertEquals("Classifier", "classifier", MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/version/artifact-version-classifier.jar", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithoutRepositoryAndGroupArtifactTypeClassifier() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact//type/classifier");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "LATEST", MvnUrlParser.getVersion());
        assertEquals("Type", "type", MvnUrlParser.getType());
        assertEquals("Classifier", "classifier", MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/LATEST/artifact-LATEST-classifier.type", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithoutRepositoryAndGroupArtifactClassifier() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact///classifier");
        assertEquals("Group", "group", MvnUrlParser.getGroup());
        assertEquals("Artifact", "artifact", MvnUrlParser.getArtifact());
        assertEquals("Version", "LATEST", MvnUrlParser.getVersion());
        assertEquals("Type", "jar", MvnUrlParser.getType());
        assertEquals("Classifier", "classifier", MvnUrlParser.getClassifier());
        assertEquals("Artifact path", "group/artifact/LATEST/artifact-LATEST-classifier.jar", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void urlWithJarRepository() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("jar:http://repository/repository.jar!/@id=fake!group/artifact/0.1.0");
        assertEquals("Artifact path", "group/artifact/0.1.0/artifact-0.1.0.jar", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void trailingSpace() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser(" http://repository/repository@id=fake!group/artifact/0.1.0");
        assertEquals("Artifact path", "group/artifact/0.1.0/artifact-0.1.0.jar", MvnUrlParser.getArtifactPath());
    }

    @Test
    public void snapshotPath() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version-SNAPSHOT");
        assertEquals("Artifact snapshot path", "group/artifact/version-SNAPSHOT/artifact-version-timestamp-build.jar",
                MvnUrlParser.getSnapshotPath("version-SNAPSHOT", "timestamp", "build"));
    }

    @Test
    public void artifactPathWithVersion() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version");
        assertEquals("Artifact path", "group/artifact/version2/artifact-version2.jar", MvnUrlParser.getArtifactPath("version2"));
    }

    @Test
    public void versionMetadataPath() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version");
        assertEquals("Version metadata path", "group/artifact/version2/maven-metadata.xml",
                MvnUrlParser.getVersionMetadataPath("version2"));
    }

    @Test
    public void artifactMetadataPath() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version");
        assertEquals("Artifact metadata path", "group/artifact/maven-metadata.xml", MvnUrlParser.getArtifactMetdataPath());
    }

    @Test
    public void artifactLocalMetadataPath() throws MalformedURLException {
        MvnUrlParser MvnUrlParser = new MvnUrlParser("group/artifact/version");
        assertEquals("Artifact local metadata path", "group/artifact/maven-metadata-local.xml",
                MvnUrlParser.getArtifactLocalMetdataPath());
    }

}
