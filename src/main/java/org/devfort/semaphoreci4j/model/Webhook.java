package org.devfort.semaphoreci4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Webhook extends Model {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("url")
    private String url;
    @JsonProperty("hook_type")
    private String type;

    /**
     * Returns the webhook ID.
     *
     * @return Webhook id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the webhook URL.
     *
     * @return Webhook URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the webhook type. Can be one of the following: {@code post_build}, {@code post_deploy}, {@code all}.
     *
     * @return Webhook type.
     */
    public String getType() {
        return type;
    }

    public enum Type {
        POST_BUILD("post_build"),
        POST_DEPLOY("post_deploy"),
        ALL("all");

        private String name;

        private Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Webhook webhook = (Webhook) o;
        return Objects.equals(id, webhook.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
