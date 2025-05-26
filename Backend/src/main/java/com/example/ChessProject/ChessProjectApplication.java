package com.example.ChessProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.ChessProject.data.UserRepository;
import com.example.ChessProject.model.User;

@SpringBootApplication
public class ChessProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessProjectApplication.class, args);
		UserRepository userRepository = new UserRepository();
	}

}
