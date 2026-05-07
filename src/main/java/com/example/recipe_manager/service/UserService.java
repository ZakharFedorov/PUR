package com.example.recipe_manager.service;

import com.example.recipe_manager.dto.RegisterRequest;
import com.example.recipe_manager.entity.User;
import com.example.recipe_manager.exception.EmailAlreadyExistsException;
import com.example.recipe_manager.exception.UsernameAlreadyExistsException;
import com.example.recipe_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        String username = request.getUsername().trim().toLowerCase();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(username);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            handleDuplicateUserData(e);
        }
    }

    private void handleDuplicateUserData(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();

        if (message != null && message.contains("email")) {
            throw new EmailAlreadyExistsException("");
        }

        if (message != null && message.contains("username")) {
            throw new UsernameAlreadyExistsException("");
        }

        throw e;
    }
}