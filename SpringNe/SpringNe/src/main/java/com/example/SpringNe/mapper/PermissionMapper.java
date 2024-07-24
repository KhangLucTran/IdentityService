package com.example.SpringNe.mapper;

import com.example.SpringNe.dto.request.PermissionRequest;
import com.example.SpringNe.dto.response.PermissionResponse;
import com.example.SpringNe.entity.Permisstion;
import org.springframework.stereotype.Component;


@Component
public class PermissionMapper {
    public Permisstion toPermission(PermissionRequest request){
        return  Permisstion.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public PermissionResponse toPermissionResponse(Permisstion permission){
        return PermissionResponse.builder()
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
