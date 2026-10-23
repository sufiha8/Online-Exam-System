# Friend 2 Admin Module — Integrated with Friend 1 Student Module

This project preserves Friend 1's package layout and technology stack: Java 17, JavaFX 21.0.2, Maven, JDBC, and MySQL Connector/J 8.4.0. Source files are under `src/` (not `src/main/java`) because Friend 1's `pom.xml` uses `<sourceDirectory>src</sourceDirectory>`. Run with `mvn clean javafx:run` from the project root.

## Features added for Friend 2
- Admin login from the shared login screen
- Admin dashboard
- Create, edit, publish/unpublish, list, and delete exams
- Add, edit, list, and delete four-option questions
- View all student results and filter by student ID or exam ID

## Shared database contract
Uses the same `online_exam` database and existing tables from Friend 1:
- `students(student_id, name, email, password, phone, created_at)`
- `exams(exam_id, title, description, duration_minutes, is_available, created_at)`
- `questions(question_id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer, created_at)`
- `results(result_id, student_id, exam_id, score, total_marks, percentage, submitted_at)`

Adds one table:
- `admins(admin_id, name, email, password, is_active, created_at)`

The admin module does not rename or replace Friend 1's tables or model classes. Exam availability is `is_available`; students already query `WHERE e.is_available=1`. Questions use A/B/C/D and the student module awards one mark per question because the shared schema has no per-question marks column.

## Setup
1. In MySQL Workbench, open and execute the entire `database/online_exam.sql` script. It creates `online_exam`, existing student-side tables/sample exam, and the admins table. If the student tables already exist, `CREATE TABLE IF NOT EXISTS` keeps them.
2. Open `src/util/DatabaseConnection.java` and set `PASSWORD` to your own MySQL password. The uploaded Friend 1 version uses `root` as the password; don't assume that matches your machine.
3. Open the project root in VS Code (the folder containing `pom.xml`).
4. Run `mvn clean javafx:run`.
5. Click **Admin Login** on the login screen.

Demo admin login: `admin@onlineexam.com` / `admin123`.
These demo credentials are plain text to match the simplicity of Friend 1's current student login implementation. This is for a local college demo only, not production. Change the password and implement salted password hashing before real deployment.

## Important integration note
Friend 1's current student login and registration are still unchanged in behavior. Admin-created published exams and questions use the same tables that the student module reads; student submissions are written to the same `results` table that the admin reports screen reads. Avoid making a second copy of the database.
