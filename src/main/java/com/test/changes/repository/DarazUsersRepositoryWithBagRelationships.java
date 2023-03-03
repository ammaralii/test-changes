package com.test.changes.repository;

import com.test.changes.domain.DarazUsers;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface DarazUsersRepositoryWithBagRelationships {
    Optional<DarazUsers> fetchBagRelationships(Optional<DarazUsers> darazUsers);

    List<DarazUsers> fetchBagRelationships(List<DarazUsers> darazUsers);

    Page<DarazUsers> fetchBagRelationships(Page<DarazUsers> darazUsers);
}
