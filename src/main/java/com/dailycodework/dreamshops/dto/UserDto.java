package com.dailycodework.dreamshops.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private List<OrderDto> orders;
    private CartDto cart;
}
