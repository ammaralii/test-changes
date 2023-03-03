package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ColorsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Colors.class);
        Colors colors1 = new Colors();
        colors1.setId(1L);
        Colors colors2 = new Colors();
        colors2.setId(colors1.getId());
        assertThat(colors1).isEqualTo(colors2);
        colors2.setId(2L);
        assertThat(colors1).isNotEqualTo(colors2);
        colors1.setId(null);
        assertThat(colors1).isNotEqualTo(colors2);
    }
}
