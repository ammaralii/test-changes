package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentMethodsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentMethods.class);
        PaymentMethods paymentMethods1 = new PaymentMethods();
        paymentMethods1.setId(1L);
        PaymentMethods paymentMethods2 = new PaymentMethods();
        paymentMethods2.setId(paymentMethods1.getId());
        assertThat(paymentMethods1).isEqualTo(paymentMethods2);
        paymentMethods2.setId(2L);
        assertThat(paymentMethods1).isNotEqualTo(paymentMethods2);
        paymentMethods1.setId(null);
        assertThat(paymentMethods1).isNotEqualTo(paymentMethods2);
    }
}
