package org.devfort.semaphoreci4j.model;

import org.devfort.semaphoreci4j.BaseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DeployTest extends BaseTest {

    private Deploy deploy;

    @Override
    public void setUp() {
        super.setUp();
        try {
            Project project = semaphore.getProject("semaphoreci4j").get();
            Server server = project.getServers().stream().findFirst().get();
            deploy = server.getServerStatus().get();

            assertNotNull(project);
            assertNotNull(server);
            assertNotNull(deploy);
        } catch (IOException e) {
            fail("Exception thrown on initialization.");
        }
    }

    @Test
    public void testDeployFields() throws IOException {
        assertNotNull(deploy.getNumber());
        assertEquals(deploy.getNumber(), Long.valueOf(2));
        assertNotNull(deploy.getResult());
        assertEquals(deploy.getResult(), "passed");
        assertNotNull(deploy.getCreatedAt());
        assertEquals(deploy.getCreatedAt(), "2017-12-26T12:11:23+01:00");
        assertNotNull(deploy.getUpdatedAt());
        assertEquals(deploy.getUpdatedAt(), "2017-12-26T12:12:34+01:00");
        assertNotNull(deploy.getStartedAt());
        assertEquals(deploy.getStartedAt(), "2017-12-26T12:11:23+01:00");
        assertNotNull(deploy.getFinishedAt());
        assertEquals(deploy.getFinishedAt(), "2017-12-26T12:12:34+01:00");
        assertNotNull(deploy.getHtmlUrl());
        assertEquals(deploy.getHtmlUrl(), "https://semaphoreci.com/projects/project-hash-id/servers/1/deploys/2");
        assertNotNull(deploy.getUrl());
        assertEquals(deploy.getUrl(), "http://localhost:8089/projects/project-hash-id/servers/1/deploys/2?auth_token=valid-token");
        assertNotNull(deploy.getLogUrl());
        assertEquals(deploy.getLogUrl(), "http://localhost:8089/projects/project-hash-id/servers/1/deploys/2/log?auth_token=valid-token");
    }

    @Test
    public void testGetThreads() throws IOException {
        assertNotNull(deploy.getThreads());
        assertEquals(deploy.getThreads().size(), 1);

        deploy.getThreads().forEach(buildThread -> {
            assertNotNull(buildThread.getCommands());
            assertEquals(buildThread.getCommands().size(), 2);
        });
    }

}