package com.example.ChessProject.controller;

import com.example.ChessProject.model.User;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://127.0.0.1:5174")
public class UserController {

    private static final AtomicInteger guestCounter = new AtomicInteger(100000);

    @PostMapping("/guest")
    public User createGuestUser() {
        int id = guestCounter.getAndIncrement();
        User guest = new User("Guest" + id, "", "");
        guest.setUserID(id);
        return guest;
    }
}
