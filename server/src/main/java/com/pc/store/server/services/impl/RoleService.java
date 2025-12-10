package com.pc.store.server.services.impl;

import com.pc.store.server.dao.RoleRepository;
import com.pc.store.server.dto.request.RoleRequest;
import com.pc.store.server.dto.response.RoleResponse;
import com.pc.store.server.entities.Role;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RoleService {
    final RoleRepository roleRepository;
    final MongoTemplate mongoTemplate;

    @PostAuthorize("hasAuthority('ROLE_ADMIN')")
    public RoleResponse create(RoleRequest roleRequest) {
        Role role = Role.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .build();
        role = roleRepository.insert(role);
        return RoleResponse.builder()
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    @PostAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<RoleResponse> getAll() {
        var roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @PostAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(String roleName) {
        roleRepository.deleteById(roleName);
    }
}
