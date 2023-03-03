package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderDeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderDelivery.class);
        OrderDelivery orderDelivery1 = new OrderDelivery();
        orderDelivery1.setId(1L);
        OrderDelivery orderDelivery2 = new OrderDelivery();
        orderDelivery2.setId(orderDelivery1.getId());
        assertThat(orderDelivery1).isEqualTo(orderDelivery2);
        orderDelivery2.setId(2L);
        assertThat(orderDelivery1).isNotEqualTo(orderDelivery2);
        orderDelivery1.setId(null);
        assertThat(orderDelivery1).isNotEqualTo(orderDelivery2);
    }
}
