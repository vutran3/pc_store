package com.pc.store.server.dao;

import com.pc.store.server.entities.InvalidatedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvalidatedTokenRepository extends MongoRepository<InvalidatedToken, String> {}
