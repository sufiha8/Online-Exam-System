# Online Exam System — Integrated Student + Admin

A JavaFX-based online examination application with a shared MySQL database for both student and admin workflows. The project combines a student exam portal with an admin management dashboard in one integrated application.

## Tech stack

- Java 17
- JavaFX 21.0.2
- Maven
- JDBC + MySQL Connector/J 8.4.0
- MySQL database: `online_exam`

## Project structure

The source code is kept under `src/` instead of `src/main/java` because the Maven configuration uses:

- `<sourceDirectory>src</sourceDirectory>`

Core folders include:

- `src/ui` — JavaFX screens for login, dashboards, exam flow, and admin management
- `src/service` — exam and authentication business logic
- `src/dao` — database access layer
- `src/model` — entity classes for students, exams, questions, and results
- `src/util` — database connection, exam timer, and password helper utilities
- `database/online_exam.sql` — database schema and seed data

## Updated features implemented

### Student features
- Student registration with validation checks
- Student login from the main welcome screen
- Listing of published exams visible to students
- Multiple-choice exam taking flow with question randomization
- Exam timer support during assessment
- Automatic score and percentage calculation
- Result submission to the shared `results` table
- Result history view for each student
- Legacy password compatibility handling during login

### Admin features
- Admin login and admin registration from the shared UI
- Admin dashboard with action buttons for core management tasks
- Create, edit, list, and delete exams
- Publish/unpublish exam visibility for students
- Add, edit, and delete A/B/C/D questions under each exam
- View all student results
- Filter results by student ID or exam ID
- Shared data access with the student module using the same database tables

### Security and data handling
- Passwords are hashed using PBKDF2 with HMAC-SHA256 via `PasswordUtil`
- The system supports legacy plaintext passwords during migration to hashed storage
- Admin and student authentication use the same integrated application flow

## Database design

The app uses a shared `online_exam` schema with these core tables:

- `students`
- `admins`
- `exams`
- `questions`
- `results`

Important integration note:

- The student and admin modules share the same database and tables.
- Student exam data and admin-generated exam content are stored in the same `exams` / `questions` / `results` tables.
- The `is_available` field controls whether an exam is visible to students.

## Setup instructions (Windows / VS Code)

1. Open the project folder in VS Code.
2. Start MySQL and connect with MySQL Workbench.
3. Open `database/online_exam.sql` and run the script.
   - This creates the database and required tables.
   - It also inserts a sample Java Basics exam and a demo admin account if they do not already exist.
4. Open `src/util/DatabaseConnection.java` and update the database password to match your local MySQL installation.
   - The default sample value is `root`; change it if your local MySQL uses a different password.
5. From the project root (the folder containing `pom.xml`), run:

   `mvn clean javafx:run`

6. The app launches to a welcome screen where you can:
   - Student Login
   - Register Student
   - Admin Login
   - Admin Registration

## Demo login

### Admin
- Email: `admin@onlineexam.com`
- Password: `admin123`

This is a demo-only local credential for classroom use. Do not use it in production.

## Notes

- If you already have data in the `online_exam` database, back it up before running SQL changes.
- Deleting an exam cascades to related questions and results because of the current foreign-key configuration.
- This project is intended for local academic/demo use and should be hardened before production deployment.
- The current project uses password hashing, but a full production-grade security review would still recommend additional measures such as stronger password policies, account locking, and secure deployment practices.

## Sample exam

The SQL setup includes a sample exam titled `Java Basics` with multiple-choice questions so the student module can be tested immediately after setup.
