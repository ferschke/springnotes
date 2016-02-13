package com.ferschke.springnotes.repository;

import java.io.Serializable;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/** 
 * @author Oliver Ferschke
 *
 * @param <T> the entity type
 * @param <ID> the primary key type (usually long)
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, QueryDslPredicateExecutor<T> {
}
