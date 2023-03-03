package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cars.class);
        Cars cars1 = new Cars();
        cars1.setId(1L);
        Cars cars2 = new Cars();
        cars2.setId(cars1.getId());
        assertThat(cars1).isEqualTo(cars2);
        cars2.setId(2L);
        assertThat(cars1).isNotEqualTo(cars2);
        cars1.setId(null);
        assertThat(cars1).isNotEqualTo(cars2);
    }
}
