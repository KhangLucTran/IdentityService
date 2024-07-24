package com.example.SpringNe.service;


import com.example.SpringNe.dto.request.PermissionRequest;
import com.example.SpringNe.dto.response.PermissionResponse;
import com.example.SpringNe.entity.Permisstion;
import com.example.SpringNe.mapper.PermissionMapper;
import com.example.SpringNe.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest request){
        Permisstion permisstion = permissionMapper.toPermission(request);
        permisstion =permissionRepository.save(permisstion);
        return permissionMapper.toPermissionResponse(permisstion);
    }

    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void deletePermission(String permission){
        permissionRepository.deleteById(permission);
    }
}
