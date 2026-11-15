package com.example.demo;

import com.example.demo.dao.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CandidateApiIntegrationTests {

	private static final String VALID_CANDIDATE = """
			{
			  "fullName": "Asha Rao",
			  "email": "asha@example.com",
			  "phone": "9876543210",
			  "skills": "Java, Spring, SQL",
			  "experienceYears": 5,
			  "location": "Bengaluru",
			  "status": "ACTIVE"
			}
			""";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CandidateRepository candidateRepository;

	@BeforeEach
	void cleanDatabase() {
		candidateRepository.deleteAll();
	}

	@Test
	void servesBrowserApplication() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("index.html"));
	}

	@Test
	void createsCandidateAndReturnsLocation() throws Exception {
		mockMvc.perform(post("/api/candidates")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_CANDIDATE))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.email").value("asha@example.com"))
				.andExpect(jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	void rejectsInvalidCandidateInput() throws Exception {
		mockMvc.perform(post("/api/candidates")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"fullName":"", "email":"not-an-email", "experienceYears":-1, "status":"ACTIVE"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(4)));
	}

	@Test
	void rejectsPhoneNumbersThatAreNotExactlyTenDigits() throws Exception {
		mockMvc.perform(post("/api/candidates")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_CANDIDATE.replace("9876543210", "123456789")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors", hasSize(1)))
				.andExpect(jsonPath("$.errors[0]")
						.value("phone: Phone number must be exactly 10 digits"));
	}

	@Test
	void searchesAndCombinesCandidateFilters() throws Exception {
		createCandidate(VALID_CANDIDATE);
		createCandidate(VALID_CANDIDATE.replace("asha@example.com", "other@example.com")
				.replace("Bengaluru", "Chennai")
				.replace("Java, Spring, SQL", "Python")
				.replace("\"experienceYears\": 5", "\"experienceYears\": 2"));

		mockMvc.perform(get("/api/candidates")
						.param("search", "asha")
						.param("location", "bengaluru")
						.param("skill", "spring")
						.param("status", "ACTIVE")
						.param("minExperience", "4")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].fullName").value("Asha Rao"))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	void rejectsInvalidFilterRangesAndEnumValues() throws Exception {
		mockMvc.perform(get("/api/candidates")
						.param("minExperience", "8")
						.param("maxExperience", "3"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail", containsString("must not exceed")));

		mockMvc.perform(get("/api/candidates").param("status", "UNKNOWN"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void duplicateEmailReturnsConflict() throws Exception {
		createCandidate(VALID_CANDIDATE);

		mockMvc.perform(post("/api/candidates")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_CANDIDATE.replace("Asha Rao", "Another Candidate")))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.detail").value("A candidate with this email already exists."));
	}

	@Test
	void updatesAndDeletesCandidate() throws Exception {
		String location = createCandidate(VALID_CANDIDATE);
		String id = location.substring(location.lastIndexOf('/') + 1);
		String updated = VALID_CANDIDATE.replace("Asha Rao", "Asha Updated");

		mockMvc.perform(put("/api/candidates/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updated))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fullName").value("Asha Updated"));

		mockMvc.perform(delete("/api/candidates/{id}", id))
				.andExpect(status().isNoContent());
		mockMvc.perform(get("/api/candidates/{id}", id))
				.andExpect(status().isNotFound());
	}

	private String createCandidate(String candidateJson) throws Exception {
		return mockMvc.perform(post("/api/candidates")
						.contentType(MediaType.APPLICATION_JSON)
						.content(candidateJson))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getHeader("Location");
	}
}
