package com.example.demo.dao;

import com.example.demo.model.Candidate;
import com.example.demo.model.CandidateStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CandidateSpecifications {

	private CandidateSpecifications() {
	}

	public static Specification<Candidate> matching(
			String search,
			String location,
			String skill,
			CandidateStatus status,
			Integer minExperience,
			Integer maxExperience,
			String linkedIn) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (hasText(search)) {
				String pattern = containsPattern(search);
				predicates.add(criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), pattern, '\\'),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern, '\\')));
			}
			if (hasText(location)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("location")),
						containsPattern(location),
						'\\'));
			}
			if (hasText(skill)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("skills")),
						containsPattern(skill),
						'\\'));
			}
			if (status != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), status));
			}
			if (minExperience != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(
						root.get("experienceYears"), minExperience));
			}
			if (maxExperience != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(
						root.get("experienceYears"), maxExperience));
			}

			if (hasText(linkedIn)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("linkedIn")),
						containsPattern(linkedIn),
						'\\'));
			}

			return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
		};
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private static String containsPattern(String value) {
		String normalized = value.trim().toLowerCase(Locale.ROOT)
				.replace("\\", "\\\\")
				.replace("%", "\\%")
				.replace("_", "\\_");
		return "%" + normalized + "%";
	}
}
