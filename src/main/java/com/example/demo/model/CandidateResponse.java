package com.example.demo.model;

import java.time.Instant;

public record CandidateResponse(
		Long id,
		String fullName,
		String email,
		String phone,
		String skills,
		Integer experienceYears,
		String location,
		CandidateStatus status,
		Instant createdAt,
		Instant updatedAt,
		String linkedIn) {

	public static CandidateResponse from(Candidate candidate) {
		return new CandidateResponse(
				candidate.getId(),
				candidate.getFullName(),
				candidate.getEmail(),
				candidate.getPhone(),
				candidate.getSkills(),
				candidate.getExperienceYears(),
				candidate.getLocation(),
				candidate.getStatus(),
				candidate.getCreatedAt(),
				candidate.getUpdatedAt(),
				candidate.getLinkedIn());
	}
}
