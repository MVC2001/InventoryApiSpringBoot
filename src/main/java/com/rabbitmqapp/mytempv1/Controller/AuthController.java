package com.rabbitmqapp.mytempv1.Controller;

import com.rabbitmqapp.mytempv1.Dto.*;
import com.rabbitmqapp.mytempv1.Entity.Permission;
import com.rabbitmqapp.mytempv1.Entity.Role;
import com.rabbitmqapp.mytempv1.Entity.User;
import com.rabbitmqapp.mytempv1.Exception.UserNotAuthenticatedException;
import com.rabbitmqapp.mytempv1.Repository.RoleRepository;
import com.rabbitmqapp.mytempv1.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    private final RoleRepository roleRepository;

    public AuthController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserSignupDTO userSignupDTO) {
        try {
            // Call the service layer to register the user
            UserResponseDTO userResponseDTO = userService.registerUser(userSignupDTO);

            // Return a 201 Created status with the userResponseDTO in the body
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
        } catch (RuntimeException e) {
            // Return a 400 Bad Request status with a custom message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponseDTO(null, null, null, null, null,null, e.getMessage()));
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserResponseDTO(null, null, null, null, null,null, "An unexpected error occurred"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new JwtResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
    // New endpoint to fetch the currently logged-in username
    @GetMapping("/current-username")
    public ResponseEntity<String> getCurrentUsername() {
        try {
            String username = userService.getCurrentUsername();
            return ResponseEntity.ok(username);
        } catch (UserNotAuthenticatedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            UserProfileDTO profile = userService.getCurrentUserProfile();
            return ResponseEntity.ok(profile);
        } catch (UserNotAuthenticatedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.requestPasswordReset(email);
        return ResponseEntity.ok("Reset link sent to your email.");
    }


    @PostMapping("/update-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordResetRequestDTO request) {
        try {
            String responseMessage = userService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(responseMessage);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }



    @GetMapping("/users")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        try {
            List<UserProfileDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get user by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable("id") Long userId) {
        try {
            UserProfileDTO userProfile = userService.getUserById(userId);
            return ResponseEntity.ok(userProfile);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PutMapping("/users/{id}")
  
    public ResponseEntity<User> editUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.editUser(id, userDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        try {
            long count = userService.countUsers();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        try {
            List<Role> roles = userService.getAllRoles();
            List<RoleDTO> roleDTOs = roles.stream()
                    .map(role -> new RoleDTO(role.getId(), role.getName(), role.getDescription()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(roleDTOs);
        } catch (Exception e) {
            // Log the error (optional)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // You can also return a custom error message or object
        }
    }
    @PostMapping("/add-role") // Change the URL to /add-role
    public ResponseEntity<Role> addRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role newRole = new Role();
        newRole.setName(roleDTO.getName());
        newRole.setDescription(roleDTO.getDescription());

        Role savedRole = roleRepository.save(newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable("id") Long roleId) {
        try {
            RoleDTO role = userService.getRoleById(roleId);
            return ResponseEntity.ok(role);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/rolesAsDropDown")
    public List<RoleDTO> getRoles() {
        return userService.fetchRolesAsDropdown();
    }
    @PutMapping("/roles/{id}")
    public ResponseEntity<Role> editRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        try {
            Role role = userService.editRole(id, roleDTO.getName(), roleDTO.getDescription());
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/roles/count")
    public ResponseEntity<Long> getRoleCount() {
        try {
            long count = userService.countRoles();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        try {
            userService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found.");
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete role as it is associated with existing users.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<Permission> permissions = userService.getAllPermissions();
        List<PermissionDTO> permissionDTOs = permissions.stream()
                .map(permission -> new PermissionDTO(permission.getId(), permission.getName(), permission.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDTOs);
    }


    @PostMapping("/add-permission")
    public ResponseEntity<PermissionDTO> addPermission(@RequestBody @NotBlank PermissionDTO permissionDTO) {
        try {
            Permission permission = userService.addPermission(permissionDTO.getName(), permissionDTO.getDescription());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new PermissionDTO(permission.getId(), permission.getName(), permission.getDescription()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Get permission by ID
    @GetMapping("/permission/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable("id") Long permissionId) {
        try {
            PermissionDTO permission = userService.getPermissionById(permissionId);
            return ResponseEntity.ok(permission);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/permissions/{id}")
    public ResponseEntity<PermissionDTO> editPermission(@PathVariable Long id, @RequestBody @NotBlank PermissionDTO permissionDTO) {
        try {
            Permission permission = userService.editPermission(id, permissionDTO.getName(), permissionDTO.getDescription());
            return ResponseEntity.ok(new PermissionDTO(permission.getId(), permission.getName(), permission.getDescription()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        try {
            userService.deletePermission(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/permissions/count")
    public ResponseEntity<Long> getPermissionCount() {
        try {
            long count = userService.countPermissions();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                userService.logoutUser(token);
                return ResponseEntity.ok("Logout successful");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid authorization header");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout");
        }
    }

}
