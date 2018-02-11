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
import java.util.Objects;
import java.util.Set;

/**
 * @author sokolovic
 */
public class Build extends Model {

    @JsonProperty("build_url")
    private String url;
    @JsonProperty("build_info_url")
    private String infoUrl;
    @JsonProperty("build_log_url")
    private String logUrl;
    @JsonProperty("build_number")
    private Long number;
    @JsonProperty("result")
    private String result;
    @JsonProperty("started_at")
    private String startedAt;
    @JsonProperty("finished_at")
    private String finishedAt;

    private Set<Commit> commits;
    private Set<BuildThread> threads;

    /**
     * Returns the build URL.
     *
     * @return Build URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the build info URL.
     *
     * @return Build info URL.
     */
    public String getInfoUrl() {
        return infoUrl;
    }

    /**
     * Returns the build log URL.
     *
     * @return Build log URL.
     */
    public String getLogUrl() {
        return logUrl;
    }

    /**
     * Returns the build number.
     *
     * @return Build number.
     */
    public Long getNumber() {
        return number;
    }

    /**
     * Returns the build result.
     *
     * @return Build result.
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the timestamp when build started.
     *
     * @return Timestamp when build started.
     */
    public String getStartedAt() {
        return startedAt;
    }

    /**
     * Returns the timestamp when build finished.
     *
     * @return Timestamp when build finished.
     */
    public String getFinishedAt() {
        return finishedAt;
    }

    /**
     * Returns the build commits.
     *
     * @return Build commits.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<Commit> getCommits() throws IOException {
        if (commits == null) {
            commits = doGetCommits();
        }
        return commits;
    }

    /**
     * Returns the build threads.
     *
     * @return Build threads.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<BuildThread> getThreads() throws IOException {
        if (threads == null) {
            threads = doGetThreads();
        }
        return threads;
    }

    /**
     * Triggers the stop of this build.
     *
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public void stopBuild() throws IOException {
        client.post(getStopBuildUrl(), null, Build.class);

        refresh();
    }

    @Override
    public void refresh() throws IOException {
        commits = doGetCommits();
        threads = doGetThreads();
    }

    /**
     * Invokes the Semaphore API in order to collect the commits.
     *
     * @return Collection of build commits.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<Commit> doGetCommits() throws IOException {
        BuildInformation buildInformation = client.get(this.infoUrl, BuildInformation.class);
        buildInformation.getCommits().forEach(commit -> commit.setClient(client));

        return buildInformation.getCommits();
    }

    /**
     * Invokes the Semaphore API in order to collect the build threads.
     *
     * @return Collection of build threads.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<BuildThread> doGetThreads() throws IOException {
        BuildLog buildLog = client.get(this.logUrl, BuildLog.class);
        buildLog.getThreads().forEach(thread -> thread.setClient(client));

        return buildLog.getThreads();
    }

    /**
     * Returns the URL for triggering the stop of this build.
     *
     * @return URL for stopping this build.
     */
    private String getStopBuildUrl() {
        return this.logUrl.replace("/log", "/stop");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Build build = (Build) o;
        return Objects.equals(number, build.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}

/**
 * Build information model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BuildInformation extends Model {

    @JsonProperty("commits")
    private Set<Commit> commits;

    public Set<Commit> getCommits() {
        return commits;
    }

}

/**
 * Build log model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BuildLog extends Model {

    @JsonProperty("threads")
    private Set<BuildThread> threads;

    public Set<BuildThread> getThreads() {
        return threads;
    }

}

