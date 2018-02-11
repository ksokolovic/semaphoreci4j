package org.devfort.semaphoreci4j.model;

import org.devfort.semaphoreci4j.BaseTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class ServerTest extends BaseTest {

    private Server server;

    @Override
    public void setUp() {
        super.setUp();
        try {
            Project project = semaphore.getProject("semaphoreci4j").get();
            server = project.getServers().stream().findAny().get();

            assertNotNull(project);
            assertNotNull(server);
        } catch (IOException e) {
            fail("Exception thrown on initialization.");
        }
    }

    @Test
    public void testServerFields() throws IOException {
        assertNotNull(server.getName());
        assertNotNull(server.getUrl());
        assertNotNull(server.getStatusUrl());
        assertNotNull(server.getHistoryUrl());
        assertNotNull(server.getDeploymentMethod());
        assertNotNull(server.getStrategy());
        assertNotNull(server.getBranchName());
    }

    @Test
    public void testGetServerDeploys() throws IOException {
        assertNotNull(server.getDeploys());
        assertEquals(server.getDeploys().size(), 2);
    }

    @Test
    public void testGetDeployByExistingNumber() throws IOException {
        Optional<Deploy> deploy = server.getDeploy(1L);

        assertTrue(deploy.isPresent());
        assertNotNull(deploy.get());
        assertEquals(deploy.get().getNumber(), Long.valueOf(1));
    }

    @Test
    public void testGetDeployByNonExistingNumber() throws IOException {
        Optional<Deploy> deploy = server.getDeploy(100L);

        assertFalse(deploy.isPresent());
    }

    @Test
    public void testGetServerStatus() throws IOException {
        Optional<Deploy> serverStatus = server.getServerStatus();

        assertTrue(serverStatus.isPresent());
        assertNotNull(serverStatus.get());
    }

}