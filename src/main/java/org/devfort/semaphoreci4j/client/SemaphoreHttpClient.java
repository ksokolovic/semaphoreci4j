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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.io.ByteStreams;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.devfort.semaphoreci4j.http.AuthenticatedHttpDelete;
import org.devfort.semaphoreci4j.http.AuthenticatedHttpGet;
import org.devfort.semaphoreci4j.http.AuthenticatedHttpPost;
import org.devfort.semaphoreci4j.http.AuthenticatedHttpPut;
import org.devfort.semaphoreci4j.model.Model;
import org.devfort.semaphoreci4j.validation.ResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * @author sokolovic
 */
public class SemaphoreHttpClient implements SemaphoreHttpConnection {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private CloseableHttpClient client;
    private HttpContext context;
    private String authToken;
    private ResponseValidator responseValidator;

    /**
     * Argument constructor.
     * <p>
     * Initializes the authenticated instance of this HTTP client with the given
     * authentication token.
     *
     * @param authToken Semaphore CI authentication token.
     */
    public SemaphoreHttpClient(String authToken) {
        this(HttpClientBuilder.create());
        this.authToken = authToken;
        this.responseValidator = new ResponseValidator();
    }

    /**
     * Argument constructor.
     * <p>
     * Initializes the instance of this client with the given {@link HttpClientBuilder}.
     *
     * @param builder Configured {@link HttpClientBuilder} to be used.
     */
    private SemaphoreHttpClient(HttpClientBuilder builder) {
        this.client = builder.build();
        this.context = new BasicHttpContext();
    }

    @Override
    public <T extends Model> T get(String url, Class<T> cls) throws IOException {
        HttpGet getRequest = new AuthenticatedHttpGet(url, authToken);
        HttpResponse response = client.execute(getRequest, context);

        LOGGER.debug("get({}), responseCode={}, response={}", getRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            if (response.containsHeader("Pagination")) {
                return getWithPagination(url, cls, response);
            }
            return objectFromResponse(cls, response);
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(getRequest);
        }
    }

    /**
     * Helper method to be used when the response is paginated. It starts with the current response
     * that we identified pagination within, and proceeds with subsequent requests until there are
     * no more pages to process.
     *
     * @param url     URL to send the requests to.
     * @param cls     Class of the response.
     * @param current Current request from which we start processing pagination.
     * @param <T>     Type of the response.
     * @return The parsed instance of the provided class, containing information merged from all
     * the pages. {@link Model#merge(Model)} of the given type is responsible for merging different
     * pages content into one response.
     * @throws IOException If an error occurs during the request.
     */
    private <T extends Model> T getWithPagination(String url, Class<T> cls, HttpResponse current) throws IOException {
        String rawPagination = current.getFirstHeader("Pagination").getValue();
        JsonNode pagination = getDefaultMapper().readTree(rawPagination);

        int totalPages = pagination.get("total_pages").asInt();
        int nextPage = pagination.get("current_page").asInt() + 1;

        if (totalPages < nextPage) {
            return objectFromResponse(cls, current);
        }

        HttpGet getRequest = new AuthenticatedHttpGet(url, authToken);
        getRequest.setURI(URI.create(getRequest.getURI().toString() + "&page=" + nextPage));
        HttpResponse response = client.execute(getRequest, context);

        LOGGER.debug("get({}), responseCode={}, response={}", getRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            return (T) objectFromResponse(cls, current).merge(getWithPagination(url, cls, response));
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(getRequest);
        }
    }

    @Override
    public <T extends Model> List<T> getList(String url, Class<T> cls) throws IOException {
        HttpGet getRequest = new AuthenticatedHttpGet(url, authToken);
        HttpResponse response = client.execute(getRequest, context);

        LOGGER.debug("get({}), responseCode={}, response={}", getRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            return objectsFromResponse(cls, response);
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(getRequest);
        }
    }

    @Override
    public String getRaw(String url) throws IOException {
        HttpGet getRequest = new AuthenticatedHttpGet(url, authToken);
        HttpResponse response = client.execute(getRequest);

        LOGGER.debug("get({}), responseCode={}, response={}", getRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            return IOUtils.toString(response.getEntity().getContent(), CharEncoding.UTF_8);
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(getRequest);
        }
    }

    @Override
    public <T extends Model, D> T post(String url, D data, Class<T> cls) throws IOException {
        HttpPost postRequest = new AuthenticatedHttpPost(url, authToken);
        if (data != null) {
            String postData = getDefaultMapper().writeValueAsString(data);
            StringEntity postDataEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);
            postRequest.setEntity(postDataEntity);
        }
        HttpResponse response = client.execute(postRequest, context);

        LOGGER.debug("post({}), responseCode={}, response={}", postRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            if (cls != null) {
                return objectFromResponse(cls, response);
            }
            return null;
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(postRequest);
        }
    }

    @Override
    public <T extends Model, D> T put(String url, D data, Class<T> cls) throws IOException {
        HttpPut putRequest = new AuthenticatedHttpPut(url, authToken);
        if (data != null) {
            String putData = getDefaultMapper().writeValueAsString(data);
            StringEntity putDataEntity = new StringEntity(putData, ContentType.APPLICATION_JSON);
            putRequest.setEntity(putDataEntity);
        }
        HttpResponse response = client.execute(putRequest, context);

        LOGGER.debug("put({}), responseCode={}, response={}", putRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            if (cls != null) {
                return objectFromResponse(cls, response);
            }
            return null;
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(putRequest);
        }
    }

    @Override
    public boolean delete(String url) throws IOException {
        HttpDelete deleteRequest = new AuthenticatedHttpDelete(url, authToken);
        HttpResponse response = client.execute(deleteRequest, context);

        LOGGER.debug("delete({}), responseCode={}, response={}", deleteRequest.getURI(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

        try {
            responseValidator.validate(response);
            return true;
        } finally {
            EntityUtils.consume(response.getEntity());
            releaseConnection(deleteRequest);
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    /**
     * Maps the raw response to the given class instance and returns the instance.
     *
     * @param cls      Class to map the response into.
     * @param response Response to map.
     * @param <T>      Type of the return value.
     * @return Mapped response.
     * @throws IOException If an error occurs during response mapping.
     */
    private <T extends Model> T objectFromResponse(Class<T> cls, HttpResponse response) throws IOException {
        ObjectMapper mapper = getDefaultMapper();
        try {
            InputStream content = response.getEntity().getContent();
            byte[] bytes = ByteStreams.toByteArray(content);
            T result = mapper.readValue(bytes, cls);

            return result;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Maps the raw response to the collection of the given class instances and returns the collection.
     *
     * @param cls      Class to map the response instances to.
     * @param response Response to map.
     * @param <T>      Type of the elements in the result collection.
     * @return The collection containing mapped response.
     * @throws IOException If an error occurs during response mapping.
     */
    private <T extends Model> List<T> objectsFromResponse(Class<T> cls, HttpResponse response) throws IOException {
        ObjectMapper mapper = getDefaultMapper();
        try {
            TypeFactory typeFactory = mapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, cls);

            InputStream content = response.getEntity().getContent();
            byte[] bytes = ByteStreams.toByteArray(content);

            return mapper.readValue(bytes, collectionType);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Releases the connection (i.e. resets internal state of the request) to make it reusable.
     *
     * @param httpRequestBase Request to release.
     */
    private void releaseConnection(HttpRequestBase httpRequestBase) {
        httpRequestBase.releaseConnection();
    }

    /**
     * Returns the default {@link ObjectMapper mapper} instance.
     *
     * @return Default {@link ObjectMapper} mapper instance.
     */
    private ObjectMapper getDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }
}
