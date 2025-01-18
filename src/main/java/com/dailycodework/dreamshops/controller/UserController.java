package com.dailycodework.dreamshops.controller;

import com.dailycodework.dreamshops.dto.UserDto;

import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.request.CreateUserRequest;
import com.dailycodework.dreamshops.request.UserUpdateRequest;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.security.entities.UserRole;
import com.dailycodework.dreamshops.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
@EnableMethodSecurity(prePostEnabled = true)
public class UserController {
    private final IUserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Success", userDto));
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse> createUser(@Valid  @RequestBody CreateUserRequest request) {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Create User Success!", userDto));

    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserUpdateRequest request, @PathVariable Long userId) {
            User user = userService.updateUser(request, userId);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Update User Success!", userDto));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("User Deleted successfully!", null));

    }

    @PostMapping("/{userId}/promote")
    public ResponseEntity<ApiResponse> promoteToAdmin(@PathVariable Long userId){
        User user = userService.promoteToAdmin(userId);
        UserDto userDto = userService.convertUserToDto(user);
        return ResponseEntity.ok(new ApiResponse("User promoted to Admin" , userDto));
    }

    @PostMapping("/{userId}/demote")
    public ResponseEntity<ApiResponse> demoteToUser(@PathVariable Long adminId){
        User user = userService.demoteToUser(adminId);
        UserDto userDto = userService.convertUserToDto(user);
        return ResponseEntity.ok(new ApiResponse("Admin demoted to user " , userDto));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse> getUsersByRole( @PathVariable UserRole role) {
        List<String> emails = userService.getUsersByRole(role);
        return ResponseEntity.ok(new ApiResponse("Success" , emails));
    }

}
