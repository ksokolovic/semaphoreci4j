package org.devfort.semaphoreci4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Command extends Model {

    @JsonProperty("name")
    private String name;
    @JsonProperty("result")
    private int result;
    @JsonProperty("output")
    private String output;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("finish_time")
    private String finishTime;
    @JsonProperty("duration")
    private String duration;

    public String getName() {
        return name;
    }

    public int getResult() {
        return result;
    }

    public String getOutput() {
        return output;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getDuration() {
        return duration;
    }

}
