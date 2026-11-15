package com.example.demo.service;

import com.example.demo.common.BadRequestException;
import com.example.demo.common.ConflictException;
import com.example.demo.common.NotFoundException;
import com.example.demo.dao.CandidateRepository;
import com.example.demo.dao.CandidateSpecifications;
import com.example.demo.model.Candidate;
import com.example.demo.model.CandidatePageResponse;
import com.example.demo.model.CandidateRequest;
import com.example.demo.model.CandidateResponse;
import com.example.demo.model.CandidateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CandidateService {

	private static final int MAX_PAGE_SIZE = 100;
	private static final List<String> SORT_FIELDS = List.of(
			"id", "fullName", "email", "experienceYears", "location", "status", "createdAt");

	private final CandidateRepository candidateRepository;

	public CandidateService(CandidateRepository candidateRepository) {
		this.candidateRepository = candidateRepository;
	}

	public CandidateResponse create(CandidateRequest request) {
		String email = normalizeEmail(request.email());
		if (candidateRepository.existsByEmailIgnoreCase(email)) {
			throw new ConflictException("A candidate with this email already exists.");
		}
		Candidate candidate = new Candidate(
				normalizeRequired(request.fullName()),
				email,
				normalizeOptional(request.phone()),
				normalizeOptional(request.skills()),
				request.experienceYears(),
				normalizeOptional(request.location()),
				request.status(),
				normalizeOptional(request.linkedIn()));
		return CandidateResponse.from(save(candidate));
	}

	@Transactional(readOnly = true)
	public CandidateResponse get(long id) {
		return CandidateResponse.from(findCandidate(id));
	}

	@Transactional(readOnly = true)
	public CandidatePageResponse search(
			String search,
			String location,
			String skill,
			CandidateStatus status,
			Integer minExperience,
			Integer maxExperience,
			int page,
			int size,
			String sortBy,
			String sortDirection,
			String linkedIn) {
		if (page < 0) {
			throw new BadRequestException("page must be zero or greater.");
		}
		if (size < 1 || size > MAX_PAGE_SIZE) {
			throw new BadRequestException("size must be between 1 and " + MAX_PAGE_SIZE + ".");
		}
		if (!SORT_FIELDS.contains(sortBy)) {
			throw new BadRequestException("sortBy must be one of: " + String.join(", ", SORT_FIELDS) + ".");
		}
		if (minExperience != null && minExperience < 0) {
			throw new BadRequestException("minExperience must be zero or greater.");
		}
		if (maxExperience != null && maxExperience < 0) {
			throw new BadRequestException("maxExperience must be zero or greater.");
		}
		if (minExperience != null && maxExperience != null && minExperience > maxExperience) {
			throw new BadRequestException("minExperience must not exceed maxExperience.");
		}

		Sort.Direction direction;
		try {
			direction = Sort.Direction.fromString(sortDirection);
		} catch (IllegalArgumentException exception) {
			throw new BadRequestException("sortDirection must be 'asc' or 'desc'.");
		}

		Page<Candidate> results = candidateRepository.findAll(
				CandidateSpecifications.matching(
						search, location, skill, status, minExperience, maxExperience, linkedIn),
				PageRequest.of(page, size, Sort.by(direction, sortBy)));
		return CandidatePageResponse.from(results);
	}

	public CandidateResponse update(long id, CandidateRequest request) {
		Candidate candidate = findCandidate(id);
		String email = normalizeEmail(request.email());
		if (candidateRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
			throw new ConflictException("A candidate with this email already exists.");
		}
		candidate.update(
				normalizeRequired(request.fullName()),
				email,
				normalizeOptional(request.phone()),
				normalizeOptional(request.skills()),
				request.experienceYears(),
				normalizeOptional(request.location()),
				request.status(),
				normalizeOptional(request.linkedIn()));
		return CandidateResponse.from(save(candidate));
	}

	public void delete(long id) {
		Candidate candidate = findCandidate(id);
		candidateRepository.delete(candidate);
	}

	private Candidate save(Candidate candidate) {
		return candidateRepository.saveAndFlush(candidate);
	}

	private Candidate findCandidate(long id) {
		return candidateRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Candidate " + id + " was not found."));
	}

	private static String normalizeRequired(String value) {
		return value.trim();
	}

	private static String normalizeEmail(String value) {
		return value.trim().toLowerCase(Locale.ROOT);
	}

	private static String normalizeOptional(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
