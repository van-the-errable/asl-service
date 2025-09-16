package com.austinscotchlovers.asl_service.users.dto;

import com.austinscotchlovers.asl_service.users.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserUpdateDto (
        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Must be a valid email address.")
        String email,

        @NotBlank(message = "Username cannot be blank.")
        String username,

        @NotBlank(message = "First name cannot be blank.")
        @Size(max = 50, message = "First name must be less than 50 characters.")
        String firstName,

        @NotBlank(message = "Last name cannot be blank.")
        @Size(max = 50, message = "Last name must be less than 50 characters.")
        String lastName,

        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 100, message = "Name must be less than 100 characters.")
        String name,

        @URL(message = "Invalid URL format.")
        String profilePictureUrl,

        @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format.")
        String phoneNumber,

        @Valid
        Address address
) {}