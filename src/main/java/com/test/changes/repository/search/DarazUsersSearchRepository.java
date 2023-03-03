package com.test.changes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.test.changes.domain.DarazUsers;
import com.test.changes.repository.DarazUsersRepository;
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
 * Spring Data Elasticsearch repository for the {@link DarazUsers} entity.
 */
public interface DarazUsersSearchRepository extends ElasticsearchRepository<DarazUsers, Long>, DarazUsersSearchRepositoryInternal {}

interface DarazUsersSearchRepositoryInternal {
    Page<DarazUsers> search(String query, Pageable pageable);

    Page<DarazUsers> search(Query query);

    void index(DarazUsers entity);
}

class DarazUsersSearchRepositoryInternalImpl implements DarazUsersSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final DarazUsersRepository repository;

    DarazUsersSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, DarazUsersRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<DarazUsers> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<DarazUsers> search(Query query) {
        SearchHits<DarazUsers> searchHits = elasticsearchTemplate.search(query, DarazUsers.class);
        List<DarazUsers> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(DarazUsers entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
