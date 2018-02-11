package org.devfort.semaphoreci4j.model;

import org.devfort.semaphoreci4j.BaseTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

public class BuildTest extends BaseTest {

    private Build build;

    @Override
    public void setUp() {
        super.setUp();
        try {
            Project project = semaphore.getProject("semaphoreci4j").get();
            Branch branch = project.getBranch("develop").get();
            build = branch.getBranchStatus().get();

            assertNotNull(project);
            assertNotNull(branch);
            assertNotNull(build);
        } catch (IOException e) {
            fail("Exception thrown on initialization.");
        }
    }

    @Test
    public void testBuildFields() throws IOException {
        assertNotNull(build.getUrl());
        assertEquals(build.getUrl(), "https://semaphoreci.com/devfort/semaphoreci4j/branches/develop/builds/35");
        assertNotNull(build.getInfoUrl());
        assertEquals(build.getInfoUrl(), "http://localhost:8089/projects/project-hash-id/1428889/builds/35?auth_token=valid-token");
        assertNotNull(build.getLogUrl());
        assertEquals(build.getLogUrl(), "http://localhost:8089/projects/project-hash-id/1428889/builds/35/log?auth_token=valid-token");
        assertNotNull(build.getNumber());
        assertEquals(build.getNumber(), Long.valueOf(35));
        assertNotNull(build.getResult());
        assertEquals(build.getResult(), "stopped");
        assertNotNull(build.getStartedAt());
        assertEquals(build.getStartedAt(), "2017-12-27T16:16:49+01:00");
        assertNotNull(build.getFinishedAt());
        assertEquals(build.getFinishedAt(), "2017-12-27T16:18:01+01:00");
    }

    @Test
    public void testGetCommits() throws IOException {
        assertNotNull(build.getCommits());
        assertEquals(build.getCommits().size(), 5);

        build.getCommits().forEach(commit -> {
            assertNotNull(commit.getId());
            assertNotNull(commit.getUrl());
            assertNotNull(commit.getAuthorName());
            assertNotNull(commit.getAuthorEmail());
            assertNotNull(commit.getMessage());
            assertNotNull(commit.getTimestamp());
        });
    }

    @Test
    public void testGetThreads() throws IOException {
        assertNotNull(build.getThreads());
        assertEquals(build.getThreads().size(), 1);

        build.getThreads().forEach(buildThread -> {
            assertNotNull(buildThread.getCommands());
            assertEquals(buildThread.getCommands().size(), 2);
        });
    }

    @Test
    public void testStopStoppableBuild() throws IOException {
        // TODO
        // I think this will require some adjustments in the implementation first
    }

    @Test
    public void testNonStoppableBuild() throws IOException {
        // TODO
        // I think this will require some adjustments in the implementation first
    }

}