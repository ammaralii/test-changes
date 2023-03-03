package com.test.changes.repository;

import com.test.changes.domain.Cars;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CarsRepositoryWithBagRelationships {
    Optional<Cars> fetchBagRelationships(Optional<Cars> cars);

    List<Cars> fetchBagRelationships(List<Cars> cars);

    Page<Cars> fetchBagRelationships(Page<Cars> cars);
}
