CREATE DATABASE IF NOT EXISTS online_exam
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE online_exam;

CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Demo administrator for local classroom use only. Change this password after setup.
INSERT INTO admins (name, email, password, is_active)
SELECT 'System Administrator', 'admin@onlineexam.com', 'admin123', TRUE
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE email='admin@onlineexam.com');

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 30,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_answer CHAR(1) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    CONSTRAINT chk_correct_answer CHECK (correct_answer IN ('A','B','C','D'))
);

CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id INT NOT NULL,
    score INT NOT NULL,
    total_marks INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

-- Sample exam and questions for testing Friend 1's student module.
INSERT INTO exams (title, description, duration_minutes, is_available)
SELECT 'Java Basics', 'Basic Java multiple-choice questions', 10, TRUE
WHERE NOT EXISTS (SELECT 1 FROM exams WHERE title = 'Java Basics');

SET @java_exam_id = (SELECT exam_id FROM exams WHERE title = 'Java Basics' LIMIT 1);

INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT @java_exam_id, 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'instanceof', 'B'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE exam_id=@java_exam_id AND question_text='Which keyword is used to inherit a class in Java?');

INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT @java_exam_id, 'Which method is the entry point of a Java application?', 'run()', 'start()', 'main()', 'init()', 'C'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE exam_id=@java_exam_id AND question_text='Which method is the entry point of a Java application?');

INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT @java_exam_id, 'Which collection does not allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'Vector', 'C'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE exam_id=@java_exam_id AND question_text='Which collection does not allow duplicate elements?');

INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT @java_exam_id, 'Which keyword creates an object in Java?', 'class', 'new', 'this', 'void', 'B'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE exam_id=@java_exam_id AND question_text='Which keyword creates an object in Java?');

INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT @java_exam_id, 'Which package contains the List interface?', 'java.io', 'java.net', 'java.util', 'java.sql', 'C'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE exam_id=@java_exam_id AND question_text='Which package contains the List interface?');
