package org.devfort.semaphoreci4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deploy extends Model {

    @JsonProperty("number")
    private Long number;
    @JsonProperty("result")
    private String result;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("started_at")
    private String startedAt;
    @JsonProperty("finished_at")
    private String finishedAt;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("deploy_url")
    private String url;
    @JsonProperty("deploy_log_url")
    private String logUrl;

    private Set<BuildThread> threads;

    /**
     * Returns the deploy number.
     *
     * @return Deploy number.
     */
    public Long getNumber() {
        return number;
    }

    /**
     * Returns the deploy result.
     *
     * @return Deploy result.
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the timestamp when the deploy has been created.
     *
     * @return Creation timestamp of the deploy.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the timestamp when the deploy has been last updated.
     *
     * @return Last update timestamp of the deploy.
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Returns the timestamp when the deploy has started.
     *
     * @return Start timestamp of the deploy.
     */
    public String getStartedAt() {
        return startedAt;
    }

    /**
     * Returns the timestamp when the deploy has finished.
     *
     * @return Finish timestamp of the deploy.
     */
    public String getFinishedAt() {
        return finishedAt;
    }

    /**
     * Returns the deploy's HTML status URL.
     *
     * @return HTML status URL.
     */
    public String getHtmlUrl() {
        return htmlUrl;
    }

    /**
     * Returns the deploy's status URL.
     *
     * @return Status URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the deploy's log URL.
     *
     * @return Log URL.
     */
    public String getLogUrl() {
        return logUrl;
    }

    /**
     * Returns the deploy threads.
     *
     * @return Deploy threads.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<BuildThread> getThreads() throws IOException {
        if (threads == null) {
            threads = doGetThreads();
        }
        return threads;
    }

    /**
     * Invokes the Semaphore API in order to collect the deploy threads.
     *
     * @return Collection of deploy threads.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<BuildThread> doGetThreads() throws IOException {
        DeployLog deployLog = client.get(this.logUrl, DeployLog.class);
        deployLog.getThreads().forEach(thread -> thread.setClient(client));

        return deployLog.getThreads();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deploy deploy = (Deploy) o;
        return Objects.equals(number, deploy.number);
    }

    @Override
    public int hashCode() {

        return Objects.hash(number);
    }
}

/**
 * Build log model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class DeployLog extends Model {

    @JsonProperty("threads")
    private Set<BuildThread> threads;

    public Set<BuildThread> getThreads() {
        return threads;
    }

}
