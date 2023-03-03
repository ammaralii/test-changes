package com.test.changes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.changes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DarazUsersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DarazUsers.class);
        DarazUsers darazUsers1 = new DarazUsers();
        darazUsers1.setId(1L);
        DarazUsers darazUsers2 = new DarazUsers();
        darazUsers2.setId(darazUsers1.getId());
        assertThat(darazUsers1).isEqualTo(darazUsers2);
        darazUsers2.setId(2L);
        assertThat(darazUsers1).isNotEqualTo(darazUsers2);
        darazUsers1.setId(null);
        assertThat(darazUsers1).isNotEqualTo(darazUsers2);
    }
}
