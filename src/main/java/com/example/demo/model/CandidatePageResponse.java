package com.example.demo.model;

import org.springframework.data.domain.Page;

import java.util.List;

public record CandidatePageResponse(
		List<CandidateResponse> content,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last) {

	public static CandidatePageResponse from(Page<Candidate> candidatePage) {
		return new CandidatePageResponse(
				candidatePage.getContent().stream().map(CandidateResponse::from).toList(),
				candidatePage.getNumber(),
				candidatePage.getSize(),
				candidatePage.getTotalElements(),
				candidatePage.getTotalPages(),
				candidatePage.isFirst(),
				candidatePage.isLast());
	}
}
