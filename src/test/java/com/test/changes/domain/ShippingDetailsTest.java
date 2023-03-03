package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShippingDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShippingDetails.class);
        ShippingDetails shippingDetails1 = new ShippingDetails();
        shippingDetails1.setId(1L);
        ShippingDetails shippingDetails2 = new ShippingDetails();
        shippingDetails2.setId(shippingDetails1.getId());
        assertThat(shippingDetails1).isEqualTo(shippingDetails2);
        shippingDetails2.setId(2L);
        assertThat(shippingDetails1).isNotEqualTo(shippingDetails2);
        shippingDetails1.setId(null);
        assertThat(shippingDetails1).isNotEqualTo(shippingDetails2);
    }
}
