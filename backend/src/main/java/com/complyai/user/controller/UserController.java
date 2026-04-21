package com.complyai.user.controller;

import com.complyai.security.SecurityUtils;
import com.complyai.user.dto.CreateUserRequest;
import com.complyai.user.dto.UserDto;
import com.complyai.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> currentUser() {
        return ResponseEntity.ok(userService.getCurrentUser(SecurityUtils.currentUser()));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(userService.listTenantUsers(SecurityUtils.currentUser()));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createTenantUser(request, SecurityUtils.currentUser()));
    }
}
