package com.example.SpringNe.service;


import com.example.SpringNe.dto.request.UserCreationRequest;
import com.example.SpringNe.dto.response.UserResponse;
import com.example.SpringNe.entity.User;
import com.example.SpringNe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1990, 1, 1);

        request = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();

//        userResponse = UserResponse.builder()
//                .id("cd684f61-20a8-47dd-ac90-a5dbe27c293f")
//                .username("john")
//                .firstName("John")
//                .lastName("Doe")
//                .dob(dob)
//                .build();

        user = User.builder()
                .id("cd684f61-20a8-47dd-ac90-a5dbe27c293f")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .roles(new HashSet<>())
                .build();

        userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .roles(new HashSet<>())
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        //WHEN
        UserResponse response = userService.createUser(request);
        // THEN
        Assertions.assertThat(response.getId()).isEqualTo(user.getId());
        Assertions.assertThat(response.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(response.getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(response.getLastName()).isEqualTo(user.getLastName());
        Assertions.assertThat(response.getDob()).isEqualTo(user.getDob());
    }
}
