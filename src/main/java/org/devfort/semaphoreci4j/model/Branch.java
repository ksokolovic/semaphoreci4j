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
 * Project branch.
 *
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch extends Model {

    @JsonProperty("branch_name")
    private String name;
    @JsonProperty("branch_url")
    private String url;
    @JsonProperty("branch_status_url")
    private String statusUrl;
    @JsonProperty("branch_history_url")
    private String historyUrl;

    private Set<Build> builds;

    /**
     * Returns the branch name.
     *
     * @return Branch name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the branch URL.
     *
     * @return Branch URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the branch status URL.
     *
     * @return Branch status URL.
     */
    public String getStatusUrl() {
        return statusUrl;
    }

    /**
     * Returns the branch history URL.
     *
     * @return Branch history URL.
     */
    public String getHistoryUrl() {
        return historyUrl;
    }

    /**
     * Returns the branch builds.
     *
     * @return Branch builds.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Set<Build> getBuilds() throws IOException {
        if (builds == null) {
            builds = doGetBuilds();
        }
        return builds;
    }

    /**
     * Returns the build with the given build number.
     *
     * @param number Number of the build to get.
     * @return Build with the given build number.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Optional<Build> getBuild(Long number) throws IOException {
        return getBuilds().stream().filter(build -> Objects.equals(build.getNumber(), number)).findFirst();
    }

    /**
     * Returns the branch status as the information contained within the last branch build.
     *
     * @return Last branch build as the branch status.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public Optional<Build> getBranchStatus() throws IOException {
        return getBuilds().stream().max(Comparator.comparingLong(Build::getNumber));
    }

    /**
     * Triggers the rebuild on the branch's last revision.
     *
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public void rebuildLastRevision() throws IOException {
        client.post(getRebuildLastRevisionUrl(), null, Build.class);

        refresh();
    }

    @Override
    public void refresh() throws IOException {
        builds = doGetBuilds();
    }

    /**
     * Invokes the Semaphore API in order to collect the builds.
     *
     * @return Collection of branch builds.
     * @throws IOException If an error occurs during Semaphore API request.
     */
    private Set<Build> doGetBuilds() throws IOException {
        BranchHistory history = client.get(this.historyUrl, BranchHistory.class);
        history.getBuilds().forEach(build -> build.setClient(client));

        return history.getBuilds();
    }

    /**
     * Returns the URL for triggering the build of the last branch revision.
     *
     * @return URL for building the last branch revision.
     */
    private String getRebuildLastRevisionUrl() {
        return this.statusUrl.replace("/status", "/build");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(name, branch.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

/**
 * Branch history model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BranchHistory extends Model {

    @JsonProperty("builds")
    private Set<Build> builds;

    public Set<Build> getBuilds() {
        return builds;
    }

    @Override
    public Model merge(Model other) {
        this.builds.addAll(((BranchHistory) other).getBuilds());

        return this;
    }

}
