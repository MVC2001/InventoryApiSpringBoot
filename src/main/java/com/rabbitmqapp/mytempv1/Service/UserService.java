package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.*;
import com.rabbitmqapp.mytempv1.Entity.Permission;
import com.rabbitmqapp.mytempv1.Entity.Role;
import com.rabbitmqapp.mytempv1.Entity.User;
import com.rabbitmqapp.mytempv1.Exception.UserNotAuthenticatedException;
import com.rabbitmqapp.mytempv1.Repository.PermissionRepository;
import com.rabbitmqapp.mytempv1.Repository.RoleRepository;
import com.rabbitmqapp.mytempv1.Repository.UserRepository;
import com.rabbitmqapp.mytempv1.Util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Check if an email already exists
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Register User
    @Transactional
    public UserResponseDTO registerUser(UserSignupDTO userSignupDTO) {
        // Check if the email already exists
        if (existsByEmail(userSignupDTO.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        // Create a new User entity
        User user = new User();
        user.setUsername(userSignupDTO.getUsername());
        user.setEmail(userSignupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userSignupDTO.getPassword()));

        // Set the user status ("is_active" or "not_active")
        user.setStatus(userSignupDTO.getStatus());

        // Fetch and assign the role to the user
        Role role = roleRepository.findById(userSignupDTO.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + userSignupDTO.getRoleId()));
        user.setRole(role);

        // Fetch and assign permissions
        Set<Permission> permissions = userSignupDTO.getPermissionIds().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + permissionId)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        try {
            // Save the new user
            User savedUser = userRepository.save(user);

            // Send message to RabbitMQ
            rabbitTemplate.convertAndSend("user_auth", "User registered: " + savedUser.getUsername());

            // Prepare permissions for the response
            Set<String> permissionNames = savedUser.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            // Build and return the UserResponseDTO
            return new UserResponseDTO(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getRole().getName(),
                    savedUser.getStatus(), // Make sure to retrieve status from the user
                    permissionNames,
                    "User account created successfully"
            );
        } catch (Exception e) {
            throw new RuntimeException("Error registering user: " + e.getMessage());
        }
    }


    // Edit User
    public User editUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        // Optionally handle password update separately
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        return userRepository.save(user);
    }

    // Delete User
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    // Login User
    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(username);
    }


    // Check User Role
    public boolean hasRole(User user, String roleName) {
        return user.getRole().getName().equals(roleName);
    }
    // Fetch User Profile
    public UserProfileDTO getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Get the username of the authenticated user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

            UserProfileDTO profileDTO = new UserProfileDTO();
            profileDTO.setUsername(user.getUsername());
            profileDTO.setEmail(user.getEmail());
            profileDTO.setRole(user.getRole().getName());
            profileDTO.setPermissions(user.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()));

            return profileDTO;
        } else {
            throw new UserNotAuthenticatedException("No authenticated user found");
        }
    }
    public long countUsers() {
        return userRepository.count();
    }
    // Reset Password
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send message to RabbitMQ
        String message = "Password reset for user: " + user.getEmail();
        rabbitTemplate.convertAndSend("user_auth", message);
    }


    public String requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        String token = jwtUtil.generateToken(user.getUsername());
        String resetLink = "http://localhost:5173/update-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, please click the link below:\n" + resetLink);

        try {
            emailSender.send(message);
        } catch (MailException e) {
            // Log the error
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Error sending reset link. Please try again.");
        }

        return token;
    }

    public void logoutUser(String token) {
        jwtUtil.blacklistToken(token);
    }
    // Fetch All Users


    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserProfileDTO profileDTO = new UserProfileDTO();
                    profileDTO.setId(user.getId()); // Set the ID here
                    profileDTO.setUsername(user.getUsername());
                    profileDTO.setEmail(user.getEmail());
                    profileDTO.setRole(user.getRole().getName());
                    profileDTO.setPermissions(user.getPermissions().stream()
                            .map(Permission::getName)
                            .collect(Collectors.toSet()));
                    profileDTO.setStatus(user.getStatus());
                    return profileDTO;
                })
                .collect(Collectors.toList());
    }
    // Get user by ID

    // Fetch user by ID
    public UserProfileDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Set<String> permissionNames = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return new UserProfileDTO(
                user.getId(),
                user.getStatus(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName(),
                permissionNames
        );
    }

    // Add a new role
    public Role addRole(String roleName, String description) {
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(description); // Set the description
        return roleRepository.save(role);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public List<RoleDTO> fetchRolesAsDropdown() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName(), role.getDescription()))
                .collect(Collectors.toList());
    }
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        return new RoleDTO(role.getId(), role.getName(), role.getDescription());
    }

    public Role editRole(Long id, String roleName, String description) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        role.setName(roleName);
        role.setDescription(description);
        return roleRepository.save(role);
    }
    public long countRoles() {
        return roleRepository.count();
    }
    // Delete role by ID
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + roleId));

        // Check if the role has any users assigned to it
        if (!role.getUsers().isEmpty()) {
            throw new IllegalStateException("Role cannot be deleted as it is assigned to one or more users.");
        }

        // Safe to delete if no users are assigned
        roleRepository.delete(role);
    }
    // Add Permission
    public Permission addPermission(String permissionName, String description) {
        Permission permission = new Permission();
        permission.setName(permissionName);
        permission.setDescription(description); // Set the description
        return permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    // Edit Permission
    public Permission editPermission(Long permissionId, String permissionName, String description) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + permissionId));
        permission.setName(permissionName);
        permission.setDescription(description); // Update the description
        return permissionRepository.save(permission);
    }

    // Fetch permission by ID
    public PermissionDTO getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + permissionId));
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(permission.getId()); // Include ID in DTO
        permissionDTO.setName(permission.getName());
        permissionDTO.setDescription(permission.getDescription()); // Include description
        return permissionDTO;
    }

    // Delete Permission
    public void deletePermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + permissionId));
        permissionRepository.delete(permission);
    }
    public long countPermissions() {
        return permissionRepository.count();
    }

    // New method to fetch the currently logged-in user's username
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        } else {
            throw new UserNotAuthenticatedException("No authenticated user found");
        }
    }
}
