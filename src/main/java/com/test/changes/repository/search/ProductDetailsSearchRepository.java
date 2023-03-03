package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.ProductDetails;
import com.test.changes.repository.ProductDetailsRepository;
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
 * Spring Data Elasticsearch repository for the {@link ProductDetails} entity.
 */
public interface ProductDetailsSearchRepository
    extends ElasticsearchRepository<ProductDetails, Long>, ProductDetailsSearchRepositoryInternal {}

interface ProductDetailsSearchRepositoryInternal {
    Page<ProductDetails> search(String query, Pageable pageable);

    Page<ProductDetails> search(Query query);

    void index(ProductDetails entity);
}

class ProductDetailsSearchRepositoryInternalImpl implements ProductDetailsSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ProductDetailsRepository repository;

    ProductDetailsSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ProductDetailsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ProductDetails> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<ProductDetails> search(Query query) {
        SearchHits<ProductDetails> searchHits = elasticsearchTemplate.search(query, ProductDetails.class);
        List<ProductDetails> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ProductDetails entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
