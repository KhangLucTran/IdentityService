package com.example.SpringNe.service;

import com.example.SpringNe.dto.request.RoleRequest;
import com.example.SpringNe.dto.response.RoleResponse;
import com.example.SpringNe.entity.Role;
import com.example.SpringNe.mapper.RoleMapper;
import com.example.SpringNe.repository.PermissionRepository;
import com.example.SpringNe.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    private final PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermisstions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse).toList();
    }

    public void deleteRole(String role){
        roleRepository.deleteById(role);
    }

}
