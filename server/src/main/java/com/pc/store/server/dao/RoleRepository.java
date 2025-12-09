package com.pc.store.server.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pc.store.server.entities.Role;

public interface RoleRepository extends MongoRepository<Role, String> {}
