package org.example.service;

import org.example.repository.UserRepository;
import org.example.model.Role;
import org.example.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public boolean registerUser(String fullName, String email, String phoneNumber, String cin, Role role,
            String password) {
        if (userRepository.existsByEmail(email)) {
            System.out.println("Email already taken.");
            return false;
        }
        if (userRepository.existsByCin(cin)) {
            System.out.println("CIN already taken.");
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(fullName, email, phoneNumber, cin, role, hashedPassword);

        return userRepository.save(newUser);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }
        return null; // Login failed
    }
}
