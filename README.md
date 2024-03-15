# Immutables Strict Builder Jackson Issue 

This is a reproducer for https://github.com/immutables/immutables/issues/1505 and demonstrates a collection field not
found when using strict builders in combination with Jackson.

## Summary
When generating with `strictBuilder = true`, there is no `@JsonProperty("lines")` annotation on any of the
methods related to collections, which makes Jackson fail with an `UnrecognizedPropertyException` for the collections.

## Detailed Example
Consider the following objects:

```java
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
```

```
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
```

When the immutable values are generated with `strictBuilder = false` the generated `ImmutableOrder.Builder` class gets
the following methods for the order lines collection:

```java
/**
     * Adds one element to {@link Order#getOrderLines() orderLines} list.
     * @param element A orderLines element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Order.Builder addOrderLines(OrderLine element) {
      this.orderLines.add(Objects.requireNonNull(element, "orderLines element"));
      return (Order.Builder) this;
    }

    /**
     * Adds elements to {@link Order#getOrderLines() orderLines} list.
     * @param elements An array of orderLines elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Order.Builder addOrderLines(OrderLine... elements) {
      for (OrderLine element : elements) {
        this.orderLines.add(Objects.requireNonNull(element, "orderLines element"));
      }
      return (Order.Builder) this;
    }


    /**
     * Sets or replaces all elements for {@link Order#getOrderLines() orderLines} list.
     * @param elements An iterable of orderLines elements
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("lines")
    public final Order.Builder orderLines(Iterable<? extends OrderLine> elements) {
      this.orderLines.clear();
      return addAllOrderLines(elements);
    }

    /**
     * Adds elements to {@link Order#getOrderLines() orderLines} list.
     * @param elements An iterable of orderLines elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Order.Builder addAllOrderLines(Iterable<? extends OrderLine> elements) {
      for (OrderLine element : elements) {
        this.orderLines.add(Objects.requireNonNull(element, "orderLines element"));
      }
      return (Order.Builder) this;
    }
```

When deserializing everything works as expected. Jackson deserializes the order lines as it finds the ` @JsonProperty("lines")` on the `orderLines(Iterable<? extends OrderLine> elements)` method.

When generated with `strictBuilder = true` the generated `ImmutableOrder.Builder` class gets
the following methods for the order lines collection:

```java
/**
     * Adds one element to {@link Order#getOrderLines() orderLines} list.
     * @param element A orderLines element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Order.Builder addOrderLines(OrderLine element) {
      this.orderLines.add(Objects.requireNonNull(element, "orderLines element"));
      return (Order.Builder) this;
    }

    /**
     * Adds elements to {@link Order#getOrderLines() orderLines} list.
     * @param elements An array of orderLines elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Order.Builder addOrderLines(OrderLine... elements) {
      for (OrderLine element : elements) {
        this.orderLines.add(Objects.requireNonNull(element, "orderLines element"));
      }
      return (Order.Builder) this;
    }
    
    /**
     * Adds elements to {@link Order#getOrderLines() orderLines} list.
     * @param elements An iterable of orderLines elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Order.Builder addAllOrderLines(Iterable<? extends OrderLine> elements) {
      for (OrderLine element : elements) {
        this.orderLines.add(Objects.requireNonNull(element, "orderLines element"));
      }
      return (Order.Builder) this;
    }
```
When deserializing Jackson fails with `com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "lines" (class com.example.Order$Builder), not marked as ignorable (one known property: "id"])
at [Source: REDACTED ('StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION' disabled); line: 3, column: 13] (through reference chain: com.example.Order$Builder["lines"])`.

The exception is thrown because there is no method annotated with `@JsonProperty("lines")` in the `ImmutableOrder.Builder` class. If the `addAllOrderLines(Iterable<? extends OrderLine> elements)` method get the annotation it all works again.