package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.OrderDelivery;
import com.test.changes.repository.OrderDeliveryRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link OrderDelivery} entity.
 */
public interface OrderDeliverySearchRepository
    extends ElasticsearchRepository<OrderDelivery, Long>, OrderDeliverySearchRepositoryInternal {}

interface OrderDeliverySearchRepositoryInternal {
    Page<OrderDelivery> search(String query, Pageable pageable);

    Page<OrderDelivery> search(Query query);

    void index(OrderDelivery entity);
}

class OrderDeliverySearchRepositoryInternalImpl implements OrderDeliverySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final OrderDeliveryRepository repository;

    OrderDeliverySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, OrderDeliveryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<OrderDelivery> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<OrderDelivery> search(Query query) {
        SearchHits<OrderDelivery> searchHits = elasticsearchTemplate.search(query, OrderDelivery.class);
        List<OrderDelivery> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(OrderDelivery entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
