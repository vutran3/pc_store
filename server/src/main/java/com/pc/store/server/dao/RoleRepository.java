package com.pc.store.server.dao;

import com.pc.store.server.entities.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {}
