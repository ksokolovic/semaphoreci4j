package org.devfort.semaphoreci4j.model;

import org.devfort.semaphoreci4j.BaseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class BuildThreadTest extends BaseTest {

    private BuildThread thread;

    @Override
    public void setUp() {
        super.setUp();
        try {
            Project project = semaphore.getProject("semaphoreci4j").get();
            Branch branch = project.getBranch("develop").get();
            Build build = branch.getBranchStatus().get();
            thread = build.getThreads().stream().findFirst().get();
        } catch (IOException e) {
            fail("Exception thrown on initialization.");
        }
    }

    @Test
    public void testThreadFields() {
        assertEquals(thread.getNumber(), 1);
        assertNotNull(thread.getCommands());
        assertEquals(thread.getCommands().size(), 2);

        thread.getCommands().forEach(command -> {
            assertNotNull(command.getName());
            assertEquals(command.getResult(), 0);
            assertNotNull(command.getOutput());
            assertNotNull(command.getStartTime());
            assertNotNull(command.getFinishTime());
            assertNotNull(command.getDuration());
        });
    }

}