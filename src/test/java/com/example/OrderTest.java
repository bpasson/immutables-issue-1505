package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderTest {

    @Test
    public void shouldDeserialize() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(OrderTest.class.getResourceAsStream("/order.json"), Order.class);

        // we expect the two odrer lines to be present
        Assertions.assertEquals(2, order.getOrderLines().size());
    }
}
