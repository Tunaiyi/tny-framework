package com.tny.game.data.mongodb.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 14:15
 */
@NoRepositoryBean
public interface CommonRepository<T, ID> extends MongoRepository<T, ID>, UpdatableRepository<T, ID> {

}
