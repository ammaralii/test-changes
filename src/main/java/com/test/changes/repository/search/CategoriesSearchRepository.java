package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.Categories;
import com.test.changes.repository.CategoriesRepository;
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
 * Spring Data Elasticsearch repository for the {@link Categories} entity.
 */
public interface CategoriesSearchRepository extends ElasticsearchRepository<Categories, Long>, CategoriesSearchRepositoryInternal {}

interface CategoriesSearchRepositoryInternal {
    Page<Categories> search(String query, Pageable pageable);

    Page<Categories> search(Query query);

    void index(Categories entity);
}

class CategoriesSearchRepositoryInternalImpl implements CategoriesSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CategoriesRepository repository;

    CategoriesSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CategoriesRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Categories> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Categories> search(Query query) {
        SearchHits<Categories> searchHits = elasticsearchTemplate.search(query, Categories.class);
        List<Categories> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Categories entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
