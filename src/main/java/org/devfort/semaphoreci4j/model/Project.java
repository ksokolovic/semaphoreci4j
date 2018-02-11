/*
 * Copyright (c) 2017 devfort
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.devfort.semaphoreci4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.*;

/**
 * Semaphore project.
 *
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends Model {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("hash_id")
    private String hashId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("branches")
    private Set<Branch> branches;

    private Set<Server> servers;
    private Set<Webhook> webhooks;

    /**
     * Returns the project ID.
     *
     * @return Project ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the project hash ID.
     *
     * @return Project hash ID.
     */
    public String getHashId() {
        return hashId;
    }

    /**
     * Returns the project name.
     *
     * @return Project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the project owner.
     *
     * @return Project owner.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the project creation timestamp.
     *
     * @return Project creation timestamp.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the project last update timestamp.
     *
     * @return Project last update timestamp.
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Returns the project HTML URL.
     *
     * @return Project HTML URL.
     */
    public String getHtmlUrl() {
        return htmlUrl;
    }

    /**
     * Returns the collection of project branches.
     *
     * @return Project branches.
     */
    public Set<Branch> getBranches() {
        return branches;
    }

    /**
     * Returns the project branch by its name.
     *
     * @param name Name of the branch to get.
     * @return Project branch with the given name, or empty {@code Optional} if it doesn't exist.
     */
    public Optional<Branch> getBranch(String name) {
        return branches.stream().filter(branch -> branch.getName().equals(name)).findFirst();
    }

    /**
     * Returns the project servers.
     *
     * @return Project servers.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<Server> getServers() throws IOException {
        if (servers == null) {
            servers = doGetServers();
        }
        return servers;
    }

    /**
     * Returns the project webhooks.
     *
     * @return Project webhooks.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<Webhook> getWebhooks() throws IOException {
        if (webhooks == null) {
            webhooks = doGetWebhooks();
        }
        return webhooks;
    }

    /**
     * Creates new {@link Webhook} for the project.
     *
     * @param url  Webhook URL. Must begin with the protocol, or request will fail.
     * @param type Webhook type.
     * @return Created webhook.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Webhook createWebhook(String url, Webhook.Type type) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("hook_type", type.toString());

        Webhook newHook = client.post(getWebhooksUrl(), data, Webhook.class);
        newHook.setClient(client);
        getWebhooks().add(newHook);

        return newHook;
    }

    /**
     * Updates the existing {@link Webhook} of the project.
     *
     * @param webhook Hook to update.
     * @param url     New webhook URL. Must begin with the protocol, or request will fail.
     * @param type    New webhook type.
     * @return Updated webhook.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Webhook updateWebhook(Webhook webhook, String url, Webhook.Type type) throws IOException {
        if (!webhooks.contains(webhook)) {
            return null;
        }

        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("hook_type", type.toString());

        Webhook updatedHook = client.put(getWebhooksUrl() + "/" + webhook.getId(), data, Webhook.class);
        updatedHook.setClient(client);
        getWebhooks().remove(updatedHook);      // remove the old one, identified by ID
        getWebhooks().add(updatedHook);         // add the new one

        return updatedHook;
    }

    /**
     * Removes the existing {@link Webhook} from the project.
     *
     * @param webhook {@link Webhook} to remove.
     * @return {@code true} of the hook is removed; {@code false} otherwise.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public boolean deleteWebhook(Webhook webhook) throws IOException {
        if (!webhooks.contains(webhook)) {
            return false;
        }

        client.delete(getWebhooksUrl() + "/" + webhook.getId());
        webhooks.remove(webhook);

        return true;
    }

    /**
     * Invokes the Semaphore API in order to collect project servers.
     *
     * @return Collection of project servers.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<Server> doGetServers() throws IOException {
        List<ProjectServer> projectServers = client.getList(getServersUrl(), ProjectServer.class);
        Set<Server> servers = new HashSet<>();

        for (ProjectServer ps : projectServers) {
            Server server = client.get(ps.getStatusUrl(), Server.class);
            server.setClient(client);
            servers.add(server);
        }

        return servers;
    }

    /**
     * Returns the API URL of project servers.
     *
     * @return Project servers API URL.
     */
    private String getServersUrl() {
        return getRootApiUrl() + "/projects/" + hashId + "/servers";
    }

    /**
     * Invokes the Semaphore API in order to collect project webhooks.
     *
     * @return Collection of project webhooks.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<Webhook> doGetWebhooks() throws IOException {
        Set<Webhook> webhooks = new HashSet<>(client.getList(getWebhooksUrl(), Webhook.class));
        webhooks.forEach(webhook -> webhook.setClient(client));

        return webhooks;
    }

    /**
     * Returns the API URL of project webhooks.
     *
     * @return Project webhooks API URL.
     */
    private String getWebhooksUrl() {
        return getRootApiUrl() + "/projects/" + hashId + "/hooks";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) &&
                Objects.equals(hashId, project.hashId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hashId);
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ProjectServer extends Model {

    @JsonProperty("server_url")
    private String statusUrl;

    /**
     * Returns the server status URL.
     *
     * @return Server status URL.
     */
    public String getStatusUrl() {
        return statusUrl;
    }
}
