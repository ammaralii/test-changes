package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customers.class);
        Customers customers1 = new Customers();
        customers1.setId(1L);
        Customers customers2 = new Customers();
        customers2.setId(customers1.getId());
        assertThat(customers1).isEqualTo(customers2);
        customers2.setId(2L);
        assertThat(customers1).isNotEqualTo(customers2);
        customers1.setId(null);
        assertThat(customers1).isNotEqualTo(customers2);
    }
}
