package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.OrderDetails;
import com.test.changes.repository.OrderDetailsRepository;
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
 * Spring Data Elasticsearch repository for the {@link OrderDetails} entity.
 */
public interface OrderDetailsSearchRepository extends ElasticsearchRepository<OrderDetails, Long>, OrderDetailsSearchRepositoryInternal {}

interface OrderDetailsSearchRepositoryInternal {
    Page<OrderDetails> search(String query, Pageable pageable);

    Page<OrderDetails> search(Query query);

    void index(OrderDetails entity);
}

class OrderDetailsSearchRepositoryInternalImpl implements OrderDetailsSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final OrderDetailsRepository repository;

    OrderDetailsSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, OrderDetailsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<OrderDetails> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<OrderDetails> search(Query query) {
        SearchHits<OrderDetails> searchHits = elasticsearchTemplate.search(query, OrderDetails.class);
        List<OrderDetails> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(OrderDetails entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
