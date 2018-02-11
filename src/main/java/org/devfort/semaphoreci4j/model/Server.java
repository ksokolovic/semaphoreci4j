package org.devfort.semaphoreci4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.*;

/**
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Server extends Model {

    @JsonProperty("server_name")
    private String name;
    @JsonProperty("server_url")
    private String url;
    @JsonProperty("server_status_url")
    private String statusUrl;
    @JsonProperty("server_history_url")
    private String historyUrl;
    @JsonProperty("deployment_method")
    private String deploymentMethod;
    @JsonProperty("strategy")
    private String strategy;
    @JsonProperty("branch_name")
    private String branchName;

    private Set<Deploy> deploys;

    /**
     * Returns the server name.
     *
     * @return Server name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the server URL.
     *
     * @return Server URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the server status URL.
     *
     * @return Server status URL.
     */
    public String getStatusUrl() {
        return statusUrl;
    }

    /**
     * Returns the server history URL.
     *
     * @return Server history URL.
     */
    public String getHistoryUrl() {
        return historyUrl;
    }

    /**
     * Returns the server deployment method.
     *
     * @return Server deployment method.
     */
    public String getDeploymentMethod() {
        return deploymentMethod;
    }

    /**
     * Returns the server strategy.
     *
     * @return Server strategy.
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Returns the server branch name.
     *
     * @return Server branch name.
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Returns the server deploys.
     *
     * @return Server deploys.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<Deploy> getDeploys() throws IOException {
        if (deploys == null) {
            deploys = doGetDeploys();
        }
        return deploys;
    }

    /**
     * Returns the deploy with the given deploy number.
     *
     * @param number Number of the deploy to get.
     * @return Deploy with the given number.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Optional<Deploy> getDeploy(Long number) throws IOException {
        return getDeploys().stream().filter(deploy -> Objects.equals(deploy.getNumber(), number)).findFirst();
    }

    /**
     * Returns the server status as the information contained within the last deploy.
     *
     * @return Last server deploy as the server status.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Optional<Deploy> getServerStatus() throws IOException {
        return getDeploys().stream().max(Comparator.comparingLong(Deploy::getNumber));
    }

    /**
     * Stops the given deploy.
     *
     * @param deploy {@link Deploy} to stop.
     * @return Stopped deploy.
     */
    public Deploy stopDeploy(Deploy deploy) throws IOException {
        if (!deploys.contains(deploy)) {
            return null;
        }
        Deploy stoppedDeploy = client.get(getStopDeployUrl(deploy), Deploy.class);
        stoppedDeploy.setClient(client);
        getDeploys().remove(deploy);
        getDeploys().add(stoppedDeploy);

        return stoppedDeploy;
    }

    /**
     * Invokes the Semaphore API in order to collect server deploys.
     *
     * @return Collection of server deploys.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<Deploy> doGetDeploys() throws IOException {
        ServerHistory history = client.get(this.historyUrl, ServerHistory.class);
        history.getDeploys().forEach(deploy -> deploy.setClient(client));

        return history.getDeploys();
    }

    /**
     * Returns the URL to be used for stopping this deploy.
     *
     * @return Stop deploy URL.
     */
    private String getStopDeployUrl(Deploy deploy) {
        String deployUrl = deploy.getUrl().split("\\?")[0];

        return deployUrl + "/stop";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(name, server.name) &&
            Objects.equals(url, server.url) &&
            Objects.equals(statusUrl, server.statusUrl) &&
            Objects.equals(historyUrl, server.historyUrl) &&
            Objects.equals(deploymentMethod, server.deploymentMethod) &&
            Objects.equals(strategy, server.strategy) &&
            Objects.equals(branchName, server.branchName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, statusUrl, historyUrl, deploymentMethod, strategy, branchName);
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ServerHistory extends Model {

    @JsonProperty("deploys")
    private Set<Deploy> deploys;

    public Set<Deploy> getDeploys() {
        return deploys;
    }

    @Override
    public Model merge(Model other) {
        this.deploys.addAll(((ServerHistory) other).getDeploys());

        return this;
    }
}