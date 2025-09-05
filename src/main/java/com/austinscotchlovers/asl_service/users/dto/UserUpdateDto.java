package com.austinscotchlovers.asl_service.users.dto;

import com.austinscotchlovers.asl_service.users.Address;

public record UserUpdateDto (
    String email,
    String username,
    String firstName,
    String lastName,
    String name,
    String profilePictureUrl,
    String phoneNumber,
    Address address
) {}