package org.devfort.semaphoreci4j.model;

import org.devfort.semaphoreci4j.BaseTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class ProjectTest extends BaseTest {

    private Project project;

    @Override
    public void setUp() {
        super.setUp();
        try {
            project = semaphore.getProject("semaphoreci4j").get();
            assertNotNull(project);
        } catch (IOException e) {
            fail("Exception thrown on initialization.");
        }
    }

    @Test
    public void testProjectFields() throws IOException {
        assertNotNull(project.getId());
        assertEquals(project.getId(), Long.valueOf(12345));
        assertNotNull(project.getHashId());
        assertEquals(project.getHashId(), "project-hash-id");
        assertNotNull(project.getName());
        assertEquals(project.getName(), "semaphoreci4j");
        assertNotNull(project.getOwner());
        assertEquals(project.getOwner(), "devfort");
        assertNotNull(project.getCreatedAt());
        assertEquals(project.getCreatedAt(), "2017-07-22T14:25:45+02:00");
        assertNotNull(project.getUpdatedAt());
        assertEquals(project.getUpdatedAt(), "2017-12-27T17:00:11+01:00");
        assertNotNull(project.getHtmlUrl());
        assertEquals(project.getHtmlUrl(), "https://semaphoreci.com/devfort/semaphoreci4j");
        assertNotNull(project.getBranches());
        assertEquals(project.getBranches().size(), 1);
    }

    @Test
    public void testGetBranchByExistingName() throws IOException {
        Optional<Branch> branch = project.getBranch("develop");

        assertTrue(branch.isPresent());
        assertNotNull(branch.get());
        assertEquals(branch.get().getName(), "develop");
    }

    @Test
    public void testGetBranchByNonExistingName() throws IOException {
        Optional<Branch> branch = project.getBranch("non-existing-branch");

        assertFalse(branch.isPresent());
    }

    @Test
    public void testGetProjectServers() throws IOException {
        assertNotNull(project.getServers());
        assertEquals(project.getServers().size(), 1);
    }

    @Test
    public void testGetWebhooks() throws IOException {
        assertNotNull(project.getWebhooks());
        assertEquals(project.getWebhooks().size(), 1);
    }

    @Test
    public void testCreateWebhook() throws IOException {
        Webhook webhook = project.createWebhook("http://www.yahoo.com", Webhook.Type.ALL);

        assertNotNull(webhook);
        assertNotNull(webhook.getId());
        assertEquals(webhook.getUrl(), "http://www.yahoo.com");
        assertEquals(webhook.getType(), Webhook.Type.ALL.toString());

        assertEquals(project.getWebhooks().size(), 2);
    }

    @Test
    public void testUpdateExistingWebhook() throws IOException {
        int hooksCount = project.getWebhooks().size();
        Webhook toUpdate = project.getWebhooks().stream().filter(webhook -> webhook.getId() == 1).findFirst().get();

        Webhook updated = project.updateWebhook(toUpdate, "http://www.semaphoreci.com", Webhook.Type.ALL);
        assertNotNull(updated);
        assertEquals(updated.getId(), toUpdate.getId());
        assertEquals(updated.getUrl(), "http://www.semaphoreci.com");
        assertEquals(updated.getType(), Webhook.Type.ALL.toString());

        assertTrue(project.getWebhooks().contains(updated));
        assertEquals(project.getWebhooks().size(), hooksCount);
    }

    @Test
    public void testUpdateNonExistingWebhook() throws IOException {
        Webhook toUpdate = project.getWebhooks().stream().findAny().get();

        project.deleteWebhook(toUpdate);
        int hooksCount = project.getWebhooks().size();

        assertNull(project.updateWebhook(toUpdate, "https://www.foo.bar", Webhook.Type.ALL));
        assertEquals(project.getWebhooks().size(), hooksCount);
    }

    @Test
    public void testDeleteExistingWebhook() throws IOException {
        int hooksCount = project.getWebhooks().size();
        Webhook toDelete = project.getWebhooks().stream().findFirst().get();

        assertTrue(project.deleteWebhook(toDelete));
        assertEquals(project.getWebhooks().size(), hooksCount - 1);
    }

    @Test
    public void testDeleteNonExistingWebhook() throws IOException {
        Webhook toDelete = project.getWebhooks().stream().findFirst().get();

        project.deleteWebhook(toDelete);
        int hooksCount = project.getWebhooks().size();

        assertFalse(project.deleteWebhook(toDelete));
        assertEquals(project.getWebhooks().size(), hooksCount);
    }

}