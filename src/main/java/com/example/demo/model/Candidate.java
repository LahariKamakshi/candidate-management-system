package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "candidates", uniqueConstraints = {
		@UniqueConstraint(name = "uk_candidates_email", columnNames = "email")
})
public class Candidate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String fullName;

	@Column(nullable = false, length = 254)
	private String email;

	@Column(length = 30)
	private String phone;

	@Column(length = 1000)
	private String skills;

	@Column(nullable = false)
	private Integer experienceYears;

	@Column(length = 120)
	private String location;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CandidateStatus status;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	@Column(nullable = true)
	private String linkedIn;

	protected Candidate() {
	}

	public Candidate(String fullName, String email, String phone, String skills,
			Integer experienceYears, String location, CandidateStatus status, String linkedIn) {
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.skills = skills;
		this.experienceYears = experienceYears;
		this.location = location;
		this.status = status;
		this.linkedIn = linkedIn;
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public void update(String fullName, String email, String phone, String skills,
			Integer experienceYears, String location, CandidateStatus status, String linkedIn) {
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.skills = skills;
		this.experienceYears = experienceYears;
		this.location = location;
		this.status = status;
		this.linkedIn = linkedIn;
	}

	public Long getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getSkills() {
		return skills;
	}

	public Integer getExperienceYears() {
		return experienceYears;
	}

	public String getLocation() {
		return location;
	}

	public CandidateStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public String getLinkedIn() {
		return linkedIn;
	}
}
