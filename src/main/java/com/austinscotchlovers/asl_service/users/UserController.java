package com.austinscotchlovers.asl_service.users;

import com.austinscotchlovers.asl_service.users.dto.UserUpdateDto;
import com.austinscotchlovers.asl_service.users.security.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User newUser) {
        return userService.saveUser(newUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateDto updatedDto, @AuthenticationPrincipal CustomUserPrincipal principal) {
        try {
            User user = userService.updateUser(id, updatedDto);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public void deleteUser(@PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        userService.deleteUser(id);
    }
}