# Candidate Management System

A Java and Spring Boot application for managing candidate records. It includes a browser-based interface and a REST API served by the same application.

## Features

- Create, view, update, and delete candidate records
- Search candidates by name or email
- Filter by location, skill, status, experience range, or LinkedIn profile
- Paginate and sort candidate results
- Validate candidate information and reject duplicate email addresses
- Return consistent API errors for invalid requests and missing records

## Technology

- Java 21
- Spring Boot 4
- Maven Wrapper
- MySQL for application data
- H2 for automated tests
- HTML, CSS, and JavaScript for the browser interface

## Requirements

Install:

1. **Java 21 JDK**. Confirm it is available in PowerShell:

   ```powershell
   java -version
   ```

2. **MySQL Server**. MySQL Workbench is optional, but can be used to create the database.
3. **Git**, if you want to clone the repository. You do not need to install Maven; the project includes the Maven Wrapper.

## Download the project

### Option 1: Clone with Git

On the GitHub repository page, select **Code**, copy the HTTPS URL, then run in PowerShell:

```powershell
git clone https://github.com/<your-username>/<your-repository>.git
cd <your-repository>
```

Replace the URL and folder name with your repository's values. Run the following commands from the project folder containing `pom.xml`.

### Option 2: Download a ZIP

On the GitHub repository page, select **Code > Download ZIP**. Extract the ZIP, then open a PowerShell terminal in the extracted project folder containing `pom.xml`.

## Configure MySQL

Start your MySQL server and create the application database. For example, in MySQL Workbench or a MySQL command prompt, run:

```sql
CREATE DATABASE candidate_management;
```

The application defaults to `localhost:3306` and the `candidate_management` database. Set the username and password for your MySQL account in PowerShell before starting the application:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/candidate_management"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-mysql-password"
```

Replace `root` and the example password with the credentials for your MySQL account. If the account has no password, set `$env:DB_PASSWORD = ""`. These environment variables apply only to the current PowerShell window; keep using that same window to start the application. Do not commit real passwords to GitHub.

## Run the application

From the project folder, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The first run may take a few minutes while Maven downloads dependencies. When the application starts, open:

**http://localhost:8080**

Use the browser interface to manage candidates. To stop the application, press **Ctrl+C** in the PowerShell window running it.

## Run the tests

```powershell
.\mvnw.cmd test
```

The automated tests use an isolated in-memory H2 database, so they do not require MySQL to be running.

## REST API

Base URL: `http://localhost:8080/api/candidates`

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/candidates` | Create a candidate |
| `GET` | `/api/candidates/{id}` | Get one candidate |
| `GET` | `/api/candidates` | List, search, and filter candidates |
| `PUT` | `/api/candidates/{id}` | Update a candidate |
| `DELETE` | `/api/candidates/{id}` | Delete a candidate |

Candidate fields include `fullName`, `email`, `phone`, `skills`, `experienceYears`, `location`, `status`, and `linkedIn`. Status values are `ACTIVE`, `INACTIVE`, `HIRED`, and `ARCHIVED`. Email addresses must be unique.

The list endpoint supports these optional query parameters:

- `search`: partial match on name or email
- `location`, `skill`, `status`, `minExperience`, `maxExperience`, `linkedIn`: candidate filters
- `page` (default `0`) and `size` (default `20`, maximum `100`)
- `sortBy` (default `id`) and `sortDirection` (`asc` or `desc`, default `desc`)

For example:

```text
http://localhost:8080/api/candidates?search=asha&location=Bengaluru&skill=Java&minExperience=3&page=0&size=20
```

Invalid input returns `400`, a candidate that does not exist returns `404`, and a duplicate email returns `409`.

## Troubleshooting

- **Cannot connect to MySQL:** Confirm MySQL Server is running, the database exists, and `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` are correct in the same PowerShell window used to run the application.
- **Port 8080 is already in use:** Stop the other process using the port, or configure a different server port before starting the app.
- **`java` is not recognized or the wrong version appears:** Install a Java 21 JDK and make sure its `bin` directory is on your `PATH`; then open a new PowerShell window.
- **The wrapper cannot download dependencies:** Check your internet connection and try the command again.

## More project documentation

- [Technical design and scalability notes](DESIGN.md)
- [AI-assisted development process](AI_USAGE.md)

## Production note

This project is intended for local/assignment use. Before using it with real candidate data or deploying it publicly, add authentication and authorization, use secure secret management, and review database migration, HTTPS, backup, and privacy requirements.
