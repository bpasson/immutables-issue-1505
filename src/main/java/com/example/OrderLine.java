package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(overshadowImplementation = true,
        jdkOnly = true, strictBuilder = true)
@JsonDeserialize(builder = OrderLine.Builder.class)
public interface OrderLine {

    @JsonProperty("id")
    Long getId();

    @JsonProperty("description")
    String description();

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutableOrderLine.Builder {
    }
}