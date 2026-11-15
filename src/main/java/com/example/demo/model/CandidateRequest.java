package com.example.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record CandidateRequest(
		@NotBlank @Size(max = 120) String fullName,
		@NotBlank @Email @Size(max = 254) String email,
		@NotBlank(message = "Phone number is required") @Pattern(regexp = "[0-9]{10}", message = "Phone number must be exactly 10 digits") String phone,
		@Size(max = 1000) String skills,
		@NotNull @Min(0) @Max(80) Integer experienceYears,
		@Size(max = 120) String location,
		@NotNull CandidateStatus status,
		@Size(max = 254) String linkedIn) {
}
