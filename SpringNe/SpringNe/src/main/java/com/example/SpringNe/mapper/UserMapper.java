package com.example.SpringNe.mapper;

import com.example.SpringNe.dto.response.UserResponse;
import com.example.SpringNe.entity.User;

public class UserMapper {

    public UserResponse mapToUserResponse(User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setDob(user.getDob());
        //userResponse.setRoles(user.getRoles());
        return userResponse;
    }
}
