package com.austinscotchlovers.asl_service.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventDto(
        @NotBlank(message = "Name is required.")
        @Size(max = 255, message = "Name must be less than 255 characters.")
        String name,

        @Size(max = 1000, message = "Description must be less than 1000 characters.")
        String description,

        @NotNull(message = "Date is required.")
        LocalDate date,

        @NotNull(message = "Time is required.")
        LocalTime time,

        @NotBlank(message = "Location is required.")
        String location
) {}