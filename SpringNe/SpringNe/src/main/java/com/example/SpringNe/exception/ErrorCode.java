package com.example.SpringNe.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, "User existed!", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters!",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"Password must be at least {min}  characters!",HttpStatus.BAD_REQUEST),
    FIRSTNAME_INVALID(1005, "Firstname must not be blank!",HttpStatus.BAD_REQUEST),
    LASTNAME_INVALID(1006, "Lastname must not be blank!",HttpStatus.BAD_REQUEST),
    DOB_INVALID(1007, "Dob must not be null",HttpStatus.BAD_REQUEST),
    INVALID_KEY(001, "Invalid message Key!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1008, "User not existed!",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1009, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1010, "you do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1011, "Your age must be at least {min}", HttpStatus.BAD_REQUEST)
    ;

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
