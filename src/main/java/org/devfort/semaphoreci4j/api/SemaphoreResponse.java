package org.devfort.semaphoreci4j.api;

import org.devfort.semaphoreci4j.model.Model;

/**
 * Wrapper class to hold Semaphore CI response information.
 *
 * @author sokolovic
 */
public class SemaphoreResponse<T extends Model> {

    private int statusCode;
    private String statusText;
    private T response;

    public SemaphoreResponse(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public SemaphoreResponse(int statusCode, String statusText, T response) {
        this(statusCode, statusText);
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

}
