package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(overshadowImplementation = true,
        jdkOnly = true, strictBuilder = true)
@JsonDeserialize(builder = Order.Builder.class)
public interface Order {

    @JsonProperty("id")
    Long getId();

    @JsonProperty("lines")
    List<OrderLine> getOrderLines();

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutableOrder.Builder {
    }
}