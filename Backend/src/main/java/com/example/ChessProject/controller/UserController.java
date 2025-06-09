package com.example.ChessProject.controller;

import com.example.ChessProject.controller.dto.LoginRequest;
import com.example.ChessProject.controller.dto.RegisterRequest;
import com.example.ChessProject.controller.dto.UserResponse;
import com.example.ChessProject.model.User;
import com.example.ChessProject.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://127.0.0.1:5174")
public class UserController {

    private static final AtomicInteger guestCounter = new AtomicInteger(100000);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/guest")
    public User createGuestUser() {
        int id = guestCounter.getAndIncrement();
        User guest = new User("Guest" + id, "", "");
        guest.setUserID(id);
        return guest;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest req) {
        User user = userService.login(req.getUsername(), req.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest req) {
        System.out.println("Received registration request:");
        System.out.println("  Username: " + req.getUsername());
        System.out.println("  Email: " + req.getEmail());
        try {
            User user = userService.registerUser(req.getUsername(), req.getPassword(), req.getEmail());
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
