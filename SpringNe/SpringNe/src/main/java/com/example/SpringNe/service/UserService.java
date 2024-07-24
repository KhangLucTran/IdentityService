package com.example.SpringNe.service;

import com.example.SpringNe.dto.request.UserCreationRequest;
import com.example.SpringNe.dto.request.UserUpdateRequest;
import com.example.SpringNe.dto.response.UserResponse;
import com.example.SpringNe.entity.User;
import com.example.SpringNe.enums.Role;
import com.example.SpringNe.exception.AppException;
import com.example.SpringNe.exception.ErrorCode;
import com.example.SpringNe.mapper.UserMapper;
import com.example.SpringNe.repository.RoleRepository;
import com.example.SpringNe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;



    public UserResponse createUser(UserCreationRequest request) {
        log.info("Service: Create User");
        User user = new User();
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        //user.setRoles(roles);

        userRepository.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setDob(user.getDob());
        //userResponse.setRoles(user.getRoles());

        return userResponse;
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
