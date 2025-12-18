package com.pc.store.server.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pc.store.server.entities.InvalidatedToken;

public interface InvalidatedTokenRespository extends MongoRepository<InvalidatedToken, String> {}
