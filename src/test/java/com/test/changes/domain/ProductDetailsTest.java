package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductDetails.class);
        ProductDetails productDetails1 = new ProductDetails();
        productDetails1.setId(1L);
        ProductDetails productDetails2 = new ProductDetails();
        productDetails2.setId(productDetails1.getId());
        assertThat(productDetails1).isEqualTo(productDetails2);
        productDetails2.setId(2L);
        assertThat(productDetails1).isNotEqualTo(productDetails2);
        productDetails1.setId(null);
        assertThat(productDetails1).isNotEqualTo(productDetails2);
    }
}
