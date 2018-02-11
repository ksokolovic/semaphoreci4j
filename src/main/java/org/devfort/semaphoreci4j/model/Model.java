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

import org.devfort.semaphoreci4j.client.SemaphoreHttpConnection;

import java.io.IOException;

/**
 * Abstract model definition.
 *
 * @author sokolovic
 */
public abstract class Model {

    SemaphoreHttpConnection client;

    /**
     * Sets the {@link SemaphoreHttpConnection client} to use for interacting
     * with Semaphore API.
     *
     * @param client {@link SemaphoreHttpConnection} instance.
     */
    public void setClient(SemaphoreHttpConnection client) {
        this.client = client;
    }

    /**
     * Helper method for merging different objects into one.
     * <p>
     * A particular scenario where this method might come in handy is when there is a collection
     * of some type within the model itself, and the results are collected from the API in batches
     * (i.e. pagination). By redefining this method on that particular model we are able to collect
     * all the response content into one Model instance, and return it.
     *
     * @param other Other model instance to merge with.
     * @return Merged instance of this type, as defined in this method override.
     */
    public Model merge(Model other) {
        return this;
    }

    /**
     * Enforces the refresh on the given model instance, meaning that all previous
     * data stored in memory is cleared and we reach out to API in order to get the
     * fresh data.
     *
     * @throws IOException If an error occurs during Semaphore API request.
     */
    public void refresh() throws IOException {
    }

    /**
     * Returns the root API URL.
     *
     * @return Root Semaphore API URL.
     */
    public static String getRootApiUrl() {
        return "https://semaphoreci.com/api/v1";
    }

}
