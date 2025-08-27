package com.austinscotchlovers.asl_service.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{userId}")
    public String userProfile(@PathVariable Long userId, Authentication authentication) {
        return "Viewing profile for user with ID: " + userId + ". You are logged in as: " + authentication.getName() + ".";
    }
}