package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Entity.User;
import com.rabbitmqapp.mytempv1.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Directly use the role as it is assumed to be in the correct format
        String roleName = user.getRole().getName();

        // Ensure the role name does not start with "ROLE_"
        if (roleName.startsWith("ROLE_")) {
            roleName = roleName.substring(5);  // Remove "ROLE_" prefix
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(roleName)  // Pass the role directly
                .build();
    }
}
