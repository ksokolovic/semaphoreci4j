package org.devfort.semaphoreci4j;

import org.devfort.semaphoreci4j.client.SemaphoreHttpClient;
import org.devfort.semaphoreci4j.client.SemaphoreHttpConnection;
import org.devfort.semaphoreci4j.model.Model;
import org.devfort.semaphoreci4j.model.Project;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The main entry point for interacting with Semaphore CI API.
 *
 * @author sokolovic
 */
public class SemaphoreCI {

    private final String rootUrl;
    private Map<String, Project> projects;
    private SemaphoreHttpConnection client;

    /**
     * Initializes the instance of Semaphore API client with the given authentication token.
     *
     * @param authToken Semaphore API authentication token.
     * @throws IOException If an error occurs while connecting to Semaphore API.
     */
    public SemaphoreCI(String authToken) throws IOException {
        this.rootUrl = Model.getRootApiUrl() + "/projects?auth_token=" + authToken;
        this.client = new SemaphoreHttpClient(authToken);
        this.projects = doGetProjects();
    }

    /**
     * Returns all Semaphore projects as map, where project name is used as a key.
     *
     * @return Collection of projects mapped by their name.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Map<String, Project> getProjects() throws IOException {
        if (projects == null) {
            projects = doGetProjects();
        }
        return projects;
    }

    /**
     * Returns the Semaphore project by its name.
     *
     * @param name Name of the project to get.
     * @return Semaphore project with the given name, or empty {@code Optional} if it doesn't exist.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Optional<Project> getProject(String name) throws IOException {
        return getProjects().values().stream().filter(project -> project.getName().equals(name)).findFirst();
    }

    /**
     * Enforces the refresh of the given model instance, meaning that all previous
     * data stored in memory is cleared and we reach out to API in order to fetch the
     * fresh data.
     *
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public void refresh() throws IOException {
        projects = doGetProjects();
    }

    /**
     * Invokes the Semaphore API in order to collect the projects.
     *
     * @return Collection of projects mapped by their name.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Map<String, Project> doGetProjects() throws IOException {
        List<Project> projects = client.getList(rootUrl, Project.class);
        projects.forEach(project -> {
            project.setClient(client);
            project.getBranches().forEach(branch -> branch.setClient(client));
        });

        return projects.stream().collect(Collectors.toMap(Project::getName, Function.identity()));
    }

}
