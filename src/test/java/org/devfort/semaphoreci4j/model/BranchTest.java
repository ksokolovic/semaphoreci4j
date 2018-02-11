package org.devfort.semaphoreci4j.model;

import org.devfort.semaphoreci4j.BaseTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class BranchTest extends BaseTest {

    private Branch branch;

    @Override
    public void setUp() {
        super.setUp();
        try {
            Project project = semaphore.getProject("semaphoreci4j").get();
            branch = project.getBranch("develop").get();
            assertNotNull(project);
            assertNotNull(branch);
        } catch (IOException e) {
            fail("Exception thrown on initialization.");
        }
    }

    @Test
    public void testBranchFields() throws IOException {
        assertNotNull(branch.getName());
        assertEquals(branch.getName(), "develop");
        assertNotNull(branch.getUrl());
        assertEquals(branch.getUrl(), "https://semaphoreci.com/devfort/semaphoreci4j/branches/develop");
        assertNotNull(branch.getStatusUrl());
        assertEquals(branch.getStatusUrl(), "http://localhost:8089/projects/project-hash-id/1428889/status?auth_token=valid-token");
        assertNotNull(branch.getHistoryUrl());
        assertEquals(branch.getHistoryUrl(), "http://localhost:8089/projects/project-hash-id/1428889?auth_token=valid-token");
    }

    @Test
    public void testGetBranchBuilds() throws IOException {
        assertNotNull(branch.getBuilds());
        assertEquals(branch.getBuilds().size(), 35);
    }

    @Test
    public void testGetBuildByExistingNumber() throws IOException {
        Optional<Build> build = branch.getBuild(35L);

        assertTrue(build.isPresent());
        assertNotNull(build.get());
        assertEquals(build.get().getNumber(), Long.valueOf(35));
    }

    @Test
    public void testGetBuildByNonExistingNumber() throws IOException {
        Optional<Build> build = branch.getBuild(100L);

        assertFalse(build.isPresent());
    }

    @Test
    public void testGetBranchStatus() throws IOException {
        Optional<Build> branchStatus = branch.getBranchStatus();

        assertTrue(branchStatus.isPresent());
        assertNotNull(branchStatus.get());
    }

    @Test
    public void testRebuildLastRevision() throws IOException {
        // TODO
        // See BuildTest for instructions;
        // I believe this will require some adjustments in the implementation first
        branch.rebuildLastRevision();

        assertNotNull(branch.getBuilds());
    }

}