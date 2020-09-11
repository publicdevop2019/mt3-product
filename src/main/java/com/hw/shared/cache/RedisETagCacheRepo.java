package com.hw.shared.cache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisETagCacheRepo extends CrudRepository<String, String> {
}
