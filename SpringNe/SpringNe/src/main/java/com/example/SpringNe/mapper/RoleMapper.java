package com.example.SpringNe.mapper;


import com.example.SpringNe.dto.request.RoleRequest;
import com.example.SpringNe.dto.response.PermissionResponse;
import com.example.SpringNe.dto.response.RoleResponse;
import com.example.SpringNe.entity.Permisstion;
import com.example.SpringNe.entity.Role;
import com.example.SpringNe.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleMapper {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public Role toRole(RoleRequest request){
        // find Set<Permission> to add Role
        Set<Permisstion> permissions = new HashSet<>();
        if(request.getPermissions()!=null){
            for(String permissionName : request.getPermissions()){
                Permisstion permisstion = permissionRepository.findByName(permissionName);
                permissions.add(permisstion);
            }
        }

        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public RoleResponse toRoleResponse(Role role){

        Set<PermissionResponse> permissionResponses = role.getPermisstions().stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionResponses)
                .build();
    }
}
