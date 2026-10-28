-- ========================================================================
-- UniTrack — Smart Academic Calendar: Relational Database Schema (3NF)
-- Designed for MySQL 8.0+ / MariaDB / Cloud SQL
-- Demonstrates Normalization (3NF), Referential Integrity, and Cascades.
-- ========================================================================

CREATE DATABASE IF NOT EXISTS unitrack_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE unitrack_db;

-- 1. USERS Table (Base entity for Students, Teachers, and Batch Advisor)
CREATE TABLE IF NOT EXISTS USERS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
    department VARCHAR(120) DEFAULT 'Department of Computer Science & Engineering',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB;

-- 2. SEMESTERS Table (Academic Term Boundaries)
CREATE TABLE IF NOT EXISTS SEMESTERS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,       -- e.g. "Fall 2026"
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_semester_dates CHECK (end_date > start_date)
) ENGINE=InnoDB;

-- 3. COURSES Table (Academic curriculum courses taught by teachers)
CREATE TABLE IF NOT EXISTS COURSES (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- e.g. "CSE 2102"
    title VARCHAR(150) NOT NULL,      -- e.g. "Object-Oriented Design & Programming"
    instructor_id INT NOT NULL,
    semester_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id)
        REFERENCES USERS(id) ON DELETE RESTRICT,
    CONSTRAINT fk_course_semester FOREIGN KEY (semester_id)
        REFERENCES SEMESTERS(id) ON DELETE SET NULL,
    INDEX idx_course_code (code)
) ENGINE=InnoDB;

-- 4. ENROLLMENTS Table (N-to-M relationship: Students enrolled in Courses)
CREATE TABLE IF NOT EXISTS ENROLLMENTS (
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id)
        REFERENCES USERS(id) ON DELETE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_id)
        REFERENCES COURSES(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. CALENDAR_EVENTS Table (Central scheduling entity for all date-driven activities)
CREATE TABLE IF NOT EXISTS CALENDAR_EVENTS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    event_type ENUM('LECTURE', 'TEST', 'ASSIGNMENT', 'EXAM', 'HOLIDAY', 'CONSULTANCY', 'TASK') NOT NULL,
    start_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    course_id INT NULL,
    created_by INT NOT NULL,
    attendee_id INT NULL,             -- Student attendee for 1-on-1 consultancy sessions
    location VARCHAR(100),
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_course FOREIGN KEY (course_id)
        REFERENCES COURSES(id) ON DELETE SET NULL,
    CONSTRAINT fk_event_creator FOREIGN KEY (created_by)
        REFERENCES USERS(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_attendee FOREIGN KEY (attendee_id)
        REFERENCES USERS(id) ON DELETE SET NULL,
    INDEX idx_event_dates (start_date, start_time)
) ENGINE=InnoDB;

-- 6. CONSULTANCY_REQUESTS Table (Student office-hour requests submitted to faculty)
CREATE TABLE IF NOT EXISTS CONSULTANCY_REQUESTS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    teacher_id INT NOT NULL,
    request_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    topic VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'DECLINED') DEFAULT 'PENDING',
    response_note VARCHAR(255),
    calendar_event_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consult_student FOREIGN KEY (student_id)
        REFERENCES USERS(id) ON DELETE CASCADE,
    CONSTRAINT fk_consult_teacher FOREIGN KEY (teacher_id)
        REFERENCES USERS(id) ON DELETE CASCADE,
    CONSTRAINT fk_consult_event FOREIGN KEY (calendar_event_id)
        REFERENCES CALENDAR_EVENTS(id) ON DELETE SET NULL,
    INDEX idx_consult_status (status)
) ENGINE=InnoDB;

-- 7. SYLLABUS_TOPICS Table (Course curriculum topics with completion checkboxes)
CREATE TABLE IF NOT EXISTS SYLLABUS_TOPICS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    week_number INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at DATETIME NULL,
    CONSTRAINT fk_topic_course FOREIGN KEY (course_id)
        REFERENCES COURSES(id) ON DELETE CASCADE,
    INDEX idx_topic_course_week (course_id, week_number)
) ENGINE=InnoDB;

-- 8. TODO_ITEMS Table (Private personal study task checklist for students)
CREATE TABLE IF NOT EXISTS TODO_ITEMS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    task VARCHAR(255) NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    due_date DATE NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_todo_owner FOREIGN KEY (owner_id)
        REFERENCES USERS(id) ON DELETE CASCADE,
    INDEX idx_todo_owner_date (owner_id, due_date)
) ENGINE=InnoDB;

-- ========================================================================
-- Seed Initial Academic Data
-- ========================================================================
INSERT INTO USERS (name, email, password_hash, role, department) VALUES
('Alex Morgan', 'alex@uni.edu', 'pass123', 'STUDENT', 'Department of Computer Science & Engineering'),
('Dr. Sarah Jenkins', 'dr.sarah@uni.edu', 'pass123', 'TEACHER', 'Department of Computer Science & Engineering'),
('Prof. Alan Turing', 'alan.turing@uni.edu', 'pass123', 'TEACHER', 'Department of Computer Science & Engineering'),
('Prof. Tahmina Sultana', 'admin@uni.edu', 'admin123', 'ADMIN', 'Department of Computer Science & Engineering')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO SEMESTERS (name, start_date, end_date, is_active) VALUES
('Fall Semester 2026', '2026-09-01', '2026-12-31', TRUE)
ON DUPLICATE KEY UPDATE is_active=VALUES(is_active);

INSERT INTO COURSES (code, title, instructor_id, semester_id) VALUES
('CSE 2102', 'Object-Oriented Design & Programming', 2, 1),
('CSE 2104', 'Data Structures & Algorithms Lab', 3, 1),
('MATH 201', 'Discrete Mathematics', 2, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO ENROLLMENTS (student_id, course_id) VALUES
(1, 1), (1, 2), (1, 3)
ON DUPLICATE KEY UPDATE student_id=VALUES(student_id);

INSERT INTO SYLLABUS_TOPICS (course_id, week_number, title, is_completed) VALUES
(1, 1, 'Introduction to OOP & Class Modeling', TRUE),
(1, 2, 'Encapsulation, Access Modifiers, and Constructors', TRUE),
(1, 3, 'Inheritance & Polymorphic Dispatch', TRUE),
(1, 4, 'Abstract Classes & Interface Contracts', TRUE),
(1, 5, 'Java Swing GUI & Event Dispatch Thread', TRUE),
(1, 6, 'Exception Handling & Stream I/O', FALSE),
(1, 7, 'Generics & Collections Framework', FALSE),
(1, 8, 'Multithreading & ScheduledExecutorService', FALSE),
(1, 9, 'Socket Networking & Client-Server Feeds', FALSE),
(1, 10, 'JDBC & Relational Database Integration', FALSE);
