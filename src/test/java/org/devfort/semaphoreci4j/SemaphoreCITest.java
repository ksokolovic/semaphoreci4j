package org.devfort.semaphoreci4j;

import org.devfort.semaphoreci4j.model.Project;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author sokolovic
 */
public class SemaphoreCITest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInitWithInvalidAuthToken() {
        try {
            new SemaphoreCI("invalid-token");
            fail("Exception not thrown.");
        } catch (IOException e) {
            assertNotNull(e.getMessage());
            assertEquals(e.getMessage(), "Unauthorized");
        }
    }

    @Test
    public void testGetProjects() throws IOException {
        Map<String, Project> projects = semaphore.getProjects();

        assertNotNull(projects);
        assertEquals(projects.size(), 1);
    }

    @Test
    public void testGetProjectByExistingName() throws IOException {
        Optional<Project> project = semaphore.getProject("semaphoreci4j");

        assertTrue(project.isPresent());
        assertNotNull(project.get());
        assertEquals(project.get().getName(), "semaphoreci4j");
    }

    @Test
    public void testGetProjectByNonExistingName() throws IOException {
        Optional<Project> project = semaphore.getProject("non-existing-project");

        assertFalse(project.isPresent());
    }

    @Test
    public void testRefresh() throws IOException {
        semaphore.refresh();

        assertNotNull(semaphore.getProjects());
        assertEquals(semaphore.getProjects().size(), 1);
    }

}