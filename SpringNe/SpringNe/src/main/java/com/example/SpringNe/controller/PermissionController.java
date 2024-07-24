package com.example.SpringNe.controller;

import com.example.SpringNe.dto.request.ApiResponse;
import com.example.SpringNe.dto.request.PermissionRequest;
import com.example.SpringNe.dto.response.PermissionResponse;
import com.example.SpringNe.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/khangluc/permissions")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    @Autowired
    PermissionService permissionService;


    @PostMapping
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }


    @GetMapping
    ApiResponse<List<PermissionResponse>> getAllPermiApiResponse(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }


    @DeleteMapping("/{permission}")
    ApiResponse<Void> deletePermission(@PathVariable("permission") String name){
        permissionService.deletePermission(name);
        return ApiResponse.<Void>builder().build();
    }
}
