package com.test.changes.repository;

import com.test.changes.domain.DarazUsers;
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
public class DarazUsersRepositoryWithBagRelationshipsImpl implements DarazUsersRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<DarazUsers> fetchBagRelationships(Optional<DarazUsers> darazUsers) {
        return darazUsers.map(this::fetchRoles);
    }

    @Override
    public Page<DarazUsers> fetchBagRelationships(Page<DarazUsers> darazUsers) {
        return new PageImpl<>(fetchBagRelationships(darazUsers.getContent()), darazUsers.getPageable(), darazUsers.getTotalElements());
    }

    @Override
    public List<DarazUsers> fetchBagRelationships(List<DarazUsers> darazUsers) {
        return Optional.of(darazUsers).map(this::fetchRoles).orElse(Collections.emptyList());
    }

    DarazUsers fetchRoles(DarazUsers result) {
        return entityManager
            .createQuery(
                "select darazUsers from DarazUsers darazUsers left join fetch darazUsers.roles where darazUsers is :darazUsers",
                DarazUsers.class
            )
            .setParameter("darazUsers", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<DarazUsers> fetchRoles(List<DarazUsers> darazUsers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, darazUsers.size()).forEach(index -> order.put(darazUsers.get(index).getId(), index));
        List<DarazUsers> result = entityManager
            .createQuery(
                "select distinct darazUsers from DarazUsers darazUsers left join fetch darazUsers.roles where darazUsers in :darazUsers",
                DarazUsers.class
            )
            .setParameter("darazUsers", darazUsers)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
