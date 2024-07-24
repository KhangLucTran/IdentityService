package com.example.SpringNe.controller;

import com.example.SpringNe.dto.request.ApiResponse;
import com.example.SpringNe.dto.request.UserCreationRequest;
import com.example.SpringNe.dto.request.UserUpdateRequest;
import com.example.SpringNe.dto.response.UserResponse;
import com.example.SpringNe.entity.User;
import com.example.SpringNe.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/khangluc/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }


    @GetMapping("/myInfo")
    ApiResponse<User> getMyInfo(){
        return ApiResponse.<User>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping
    ApiResponse<List<User>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.getUserList());
        return apiResponse;
    }


    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        ApiResponse apiResponse = new ApiResponse();
        userService.deleteUserById(userId);
        apiResponse.setMessage("Delete user successfully!");
        return apiResponse;
    }

    @PutMapping("/{userId}")
    ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.updateUser(userId, request));
        return apiResponse;
    }

    @GetMapping("/{userId}")
    ApiResponse<User> getUserById(@PathVariable String userId) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.getUserById(userId));
        return apiResponse;
    }
}
