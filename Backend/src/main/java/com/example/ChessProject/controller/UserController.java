package com.example.ChessProject.controller;

import com.example.ChessProject.controller.dto.LoginRequest;
import com.example.ChessProject.controller.dto.RegisterRequest;
import com.example.ChessProject.controller.dto.UserResponse;
import com.example.ChessProject.model.User;
import com.example.ChessProject.service.UserService;

import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final AtomicInteger guestCounter = new AtomicInteger(100000);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/guest")
    public UserResponse createGuestUser() {
        int id = guestCounter.getAndIncrement();
        User guest = new User("Guest" + id, "", "");
        guest.setUserID(id);
        return UserResponse.fromUser(guest);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest req) {
        User user = userService.login(req.getUsername(), req.getPassword());
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return UserResponse.fromUser(user);
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest req) {
        User user = userService.registerUser(req.getUsername(), req.getPassword(), req.getEmail());
        return UserResponse.fromUser(user);
    }
}
