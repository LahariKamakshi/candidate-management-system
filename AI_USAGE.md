# AI-Assisted Development Process

AI tools were used selectively during the development of the Candidate Management System to improve development productivity and assist with specific implementation activities.

## 1. Where and How AI Was Used

Backend Development

The backend implementation was approached with a focus on Java fundamentals, object-oriented programming, layered architecture, and maintainable code. AI was used as a supporting resource to clarify Spring Boot concepts, explore implementation alternatives, and troubleshoot issues encountered during development.

Frontend Development

AI assistance was used during frontend design to explore UI layouts, improve styling, and create a simple, user-friendly interface for managing candidate information.

Automated Testing

AI was used to identify relevant test scenarios and assist in writing JUnit test cases. The generated test cases were reviewed and executed to verify application functionality, including CRUD operations, input validation, duplicate email handling, and candidate filtering.

Documentation

AI assistance was used to organize and improve the clarity of the technical documentation, application setup instructions, and scalability considerations.

## 2. Validation and Verification

AI-generated suggestions and test cases were reviewed rather than accepted without verification.

The following activities were performed:

- Executed Maven build and automated test suites.
- Reviewed compilation output and test execution results.
- Verified candidate creation, retrieval, updating, and deletion.
- Tested search and filtering with multiple criteria.
- Checked invalid input handling and duplicate email scenarios.
- Verified database persistence and application configuration.

## 3. Critical Evaluation and Engineering Decisions

AI recommendations were evaluated based on the application's requirements, maintainability, and implementation complexity.

Some important engineering decisions included:

- Selected a layered monolithic architecture to maintain clear separation of responsibilities without introducing unnecessary complexity.
- Used a lightweight frontend rather than introducing an additional frontend framework and build pipeline.
- Implemented input validation and database-level uniqueness constraints for data integrity.
- Configured database credentials using environment variables rather than committing sensitive information.
- Used H2 for automated testing to avoid depending on a locally running MySQL instance during test execution.
- Retained a simple skills representation for the initial implementation, while recognizing that a normalized relational model could be more suitable as the application grows.
- Avoided introducing microservices and additional infrastructure that were unnecessary for the current scope.

## 4. Final Review and Understanding

AI was treated as a development assistant rather than an authoritative source of implementation decisions.

The resulting application was reviewed against the assignment requirements, with attention to code quality, functionality, maintainability, and testing. The objective was to use AI effectively while maintaining an understanding of the implementation and the engineering decisions behind it.