package com.example.SpringNe.controller;

import com.example.SpringNe.dto.request.ApiResponse;
import com.example.SpringNe.dto.request.PermissionRequest;
import com.example.SpringNe.dto.request.RoleRequest;
import com.example.SpringNe.dto.response.PermissionResponse;
import com.example.SpringNe.dto.response.RoleResponse;
import com.example.SpringNe.service.PermissionService;
import com.example.SpringNe.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/khangluc/roles")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    @Autowired
    RoleService roleService;


    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }


    @GetMapping
    ApiResponse<List<RoleResponse>> getAllPermiApiResponse(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }


    @DeleteMapping("/{role}")
    ApiResponse<Void> deletePermission(@PathVariable("role") String name){
        roleService.deleteRole(name);
        return ApiResponse.<Void>builder().build();
    }
}
