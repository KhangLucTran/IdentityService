package com.example.SpringNe.exception;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppException extends RuntimeException{

    private ErrorCode  errorCode;
}
