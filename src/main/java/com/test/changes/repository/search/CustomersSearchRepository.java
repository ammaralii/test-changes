package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.Customers;
import com.test.changes.repository.CustomersRepository;
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
 * Spring Data Elasticsearch repository for the {@link Customers} entity.
 */
public interface CustomersSearchRepository extends ElasticsearchRepository<Customers, Long>, CustomersSearchRepositoryInternal {}

interface CustomersSearchRepositoryInternal {
    Page<Customers> search(String query, Pageable pageable);

    Page<Customers> search(Query query);

    void index(Customers entity);
}

class CustomersSearchRepositoryInternalImpl implements CustomersSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CustomersRepository repository;

    CustomersSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CustomersRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Customers> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Customers> search(Query query) {
        SearchHits<Customers> searchHits = elasticsearchTemplate.search(query, Customers.class);
        List<Customers> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Customers entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
