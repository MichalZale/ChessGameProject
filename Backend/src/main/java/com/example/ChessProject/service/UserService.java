package com.example.ChessProject.service;

import com.example.ChessProject.model.*;
import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import com.example.ChessProject.data.*;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private UserRepository userRepository = new UserRepository();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User login(String username, String password) {
        try{
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                if(encoder.matches(password, user.getPasswordHash()))
                    return user;
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public User registerUser(String username, String password, String email) {
        try {
            if(userRepository.findByUsername(username).isPresent()){
                throw new Exception("User already exists"); 
            }
            String passwordHash = encoder.encode(password);
            User user = new User(username, passwordHash, email);
            System.out.println("Zapisuję użytkownika");
            userRepository.saveUser(user);
            System.out.println("Zapisano użytkownika");
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
