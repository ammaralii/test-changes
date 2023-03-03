package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.ShippingDetails;
import com.test.changes.repository.ShippingDetailsRepository;
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
 * Spring Data Elasticsearch repository for the {@link ShippingDetails} entity.
 */
public interface ShippingDetailsSearchRepository
    extends ElasticsearchRepository<ShippingDetails, Long>, ShippingDetailsSearchRepositoryInternal {}

interface ShippingDetailsSearchRepositoryInternal {
    Page<ShippingDetails> search(String query, Pageable pageable);

    Page<ShippingDetails> search(Query query);

    void index(ShippingDetails entity);
}

class ShippingDetailsSearchRepositoryInternalImpl implements ShippingDetailsSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ShippingDetailsRepository repository;

    ShippingDetailsSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ShippingDetailsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ShippingDetails> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<ShippingDetails> search(Query query) {
        SearchHits<ShippingDetails> searchHits = elasticsearchTemplate.search(query, ShippingDetails.class);
        List<ShippingDetails> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ShippingDetails entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
