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

package org.devfort.semaphoreci4j.client;

import org.devfort.semaphoreci4j.model.Model;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @author sokolovic
 */
public interface SemaphoreHttpConnection extends Closeable {

    /**
     * Performs the GET request and parses the response to the instance of the given class.
     *
     * @param url URL to send the request to.
     * @param cls Class of the response.
     * @param <T> Type of the response.
     * @return The parsed instance of the provided class.
     * @throws IOException If an error occurs during the request.
     */
    <T extends Model> T get(String url, Class<T> cls) throws IOException;

    /**
     * Performs the GET request and parses the response to collection of instances of the given class.
     *
     * @param url URL to send the request to.
     * @param cls Class of the response.
     * @param <T> Type of the elements in the result collection.
     * @return The collection containing mapped response.
     * @throws IOException If an error occurs during the request.
     */
    <T extends Model> List<T> getList(String url, Class<T> cls) throws IOException;

    /**
     * Performs the GET request and returns the response as String.
     *
     * @param url URL to send the request to.
     * @return The response text.
     * @throws IOException If an error occurs during the request.
     */
    String getRaw(String url) throws IOException;

    /**
     * Performs the POST request and parses the response to the instance of the given class.
     *
     * @param url  URL to send the request to.
     * @param data Data to post.
     * @param cls  Class of the response.
     * @param <T>  Type of the response.
     * @param <D>  Type of the data to post.
     * @return The parsed instance of the provided class.
     * @throws IOException If an error occurs during the request.
     */
    <T extends Model, D> T post(String url, D data, Class<T> cls) throws IOException;

    /**
     * Performs the PUT request and parses the response to the instance of the given class.
     *
     * @param url  URL to send the request to.
     * @param data Data to put.
     * @param cls  Class of the response.
     * @param <T>  Type of the response.
     * @param <D>  Type of the data to put.
     * @return The parsed instance of the provided class.
     * @throws IOException If an error occurs during the request.
     */
    <T extends Model, D> T put(String url, D data, Class<T> cls) throws IOException;

    /**
     * Performs the DELETE request to the given endpoint.
     *
     * @param url URL to send the request to.
     * @return {@code true} if the valid response has been returned; {@code false} otherwise.
     * @throws IOException If an error occurs during the request.
     */
    boolean delete(String url) throws IOException;

}
