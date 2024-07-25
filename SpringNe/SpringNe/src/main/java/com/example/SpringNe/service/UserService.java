package com.example.SpringNe.service;

import com.example.SpringNe.constant.PredefinedRole;
import com.example.SpringNe.dto.request.RoleRequest;
import com.example.SpringNe.dto.request.UserCreationRequest;
import com.example.SpringNe.dto.request.UserUpdateRequest;
import com.example.SpringNe.dto.response.RoleResponse;
import com.example.SpringNe.dto.response.UserResponse;
import com.example.SpringNe.entity.Role;
import com.example.SpringNe.entity.User;
import com.example.SpringNe.exception.AppException;
import com.example.SpringNe.exception.ErrorCode;
import com.example.SpringNe.mapper.RoleMapper;
import com.example.SpringNe.repository.RoleRepository;
import com.example.SpringNe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        log.info("Service: Create User");

        // Check if the username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Create a new User entity
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dob(request.getDob())
                .roles(roles)
                .build();
        // Save the user to the repository
        user = userRepository.save(user);

        HashSet<RoleResponse> roleResponses = roles.stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toCollection(HashSet::new));

        // Create the UserResponse object
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .roles(roleResponses)
                .build();
    }

    // Cách Authorize bằng EnableMethodSecurity
    // Dùng Pre..... để phân quyền trên Method
    // User cso Role ADMIN thì được phép truy cập
    // kiểm tra điều kiện trước khi đi vào method

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<User> getUserList() {
        log.info("In method get All Users");
        return userRepository.findAll();
    }

    // Kiểm tra sau khi method thực hiện xong
    // Nếu thỏa đk thì trả kết quả về
    @PostAuthorize("returnObject.username == authentication.name") // nếu tên tên lấy thông tin bằng tên
    public User getUserById(String id) {                             // đang đăng nhập thì trả về kết quả
        log.info("In method Get User By Id");
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đừng cố gắng tìm kiếm, Bởi vì bạn nhập sai id rồi!"));
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }


    public User getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user;
        //Mapper
//        return UserResponse.builder()
//                .id(user.getId())
//                .username(user.getUsername())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .dob(user.getDob())
//                .roles(user.getRoles())
//                .build();
    }

    public User updateUser(String userId, UserUpdateRequest request) {
        User user = getUserById(userId);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userRepository.save(user);
    }
}
