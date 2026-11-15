package com.example.demo.controller;

import com.example.demo.model.CandidatePageResponse;
import com.example.demo.model.CandidateRequest;
import com.example.demo.model.CandidateResponse;
import com.example.demo.model.CandidateStatus;
import com.example.demo.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

	private final CandidateService candidateService;

	public CandidateController(CandidateService candidateService) {
		this.candidateService = candidateService;
	}

	@PostMapping
	public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest request) {
		CandidateResponse created = candidateService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@GetMapping("/{id}")
	public CandidateResponse get(@PathVariable long id) {
		return candidateService.get(id);
	}

	@GetMapping
	public CandidatePageResponse search(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) String skill,
			@RequestParam(required = false) CandidateStatus status,
			@RequestParam(required = false) Integer minExperience,
			@RequestParam(required = false) Integer maxExperience,
			@RequestParam(required = false) String linkedIn,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection) {
		return candidateService.search(
				search, location, skill, status, minExperience, maxExperience,
				page, size, sortBy, sortDirection, linkedIn);
	}

	@PutMapping("/{id}")
	public CandidateResponse update(
			@PathVariable long id,
			@Valid @RequestBody CandidateRequest request) {
		return candidateService.update(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		candidateService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
