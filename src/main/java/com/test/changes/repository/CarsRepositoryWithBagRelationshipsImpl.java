package com.test.changes.repository;

import com.test.changes.domain.Cars;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CarsRepositoryWithBagRelationshipsImpl implements CarsRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Cars> fetchBagRelationships(Optional<Cars> cars) {
        return cars.map(this::fetchColors);
    }

    @Override
    public Page<Cars> fetchBagRelationships(Page<Cars> cars) {
        return new PageImpl<>(fetchBagRelationships(cars.getContent()), cars.getPageable(), cars.getTotalElements());
    }

    @Override
    public List<Cars> fetchBagRelationships(List<Cars> cars) {
        return Optional.of(cars).map(this::fetchColors).orElse(Collections.emptyList());
    }

    Cars fetchColors(Cars result) {
        return entityManager
            .createQuery("select cars from Cars cars left join fetch cars.colors where cars is :cars", Cars.class)
            .setParameter("cars", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Cars> fetchColors(List<Cars> cars) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, cars.size()).forEach(index -> order.put(cars.get(index).getId(), index));
        List<Cars> result = entityManager
            .createQuery("select distinct cars from Cars cars left join fetch cars.colors where cars in :cars", Cars.class)
            .setParameter("cars", cars)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
