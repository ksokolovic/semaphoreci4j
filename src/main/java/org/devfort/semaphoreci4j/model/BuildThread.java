package org.devfort.semaphoreci4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author sokolovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildThread extends Model {

    @JsonProperty("number")
    private int number;
    @JsonProperty("commands")
    private Set<Command> commands;

    public int getNumber() {
        return number;
    }

    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildThread that = (BuildThread) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

}
