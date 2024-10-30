package com.rabbitmqapp.mytempv1.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String username;
    private String password;
    private Long roleId;
}
