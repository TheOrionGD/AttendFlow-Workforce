-- Create Database
CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;

-- Drop tables (for clean re-run)
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS students;

-- Students Table
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL
);

-- Attendance Table
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present','Absent') NOT NULL,
    UNIQUE (student_id, date), -- Prevent duplicate entries
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Insert Your Real Student Data
INSERT INTO students (name) VALUES
('ABDUL RAZEEK A'),
('ABINAYA A'),
('AFIYA J'),
('AGALYA K'),
('ARAVINDAN K'),
('ARCHANA J'),
('ARJUN S. N'),
('ARULMOZHI PRABA R'),
('ARUNA SHIVANI A S'),
('ASHMA BANU M'),
('BHARANIDHARAN K'),
('BHARATH B'),
('BHAVADHARANI A'),
('BHAWNA SRI B'),
('CROSINI INFANTEENA R'),
('DEEPIKA S'),
('DEVIPRIYA P'),
('DHANASRIDHARAN A'),
('DHARANI D V'),
('DHARANI MARI M'),
('DHARSHINI S'),
('DHIVYADHARSHINI J'),
('DIKSHA S'),
('DURGA R'),
('ELAIYANILA S'),
('ELAKHYA K'),
('ELUMALAI S'),
('ENIYAA A'),
('ESTHER JULIET S'),
('GANAGHASHREE K'),
('GANGASH S'),
('GAYATHRI M'),
('GODFREY T R'),
('GRISHNARAYANAN SATHYANARAYANAN'),
('HARI PRAKASH A'),
('HARI PRIYA E'),
('HARIHAR R'),
('HARIHARAN M'),
('HARINI M'),
('HARINI R'),
('HARINI S'),
('HARIPRIYA S'),
('HARITHA K'),
('HARIVARSHAN M'),
('HARIVARSHINI G'),
('HARSHAVARDHINI P'),
('HARSHINI K'),
('HARSHITHA R'),
('HARVEY GRAYSON J');

-- Insert Randomized Attendance for Past 5 Days
INSERT INTO attendance (student_id, date, status)
SELECT 
    s.id,
    d.att_date,
    IF(RAND() > 0.2, 'Present', 'Absent')
FROM students s
JOIN (
    SELECT '2026-04-02' AS att_date
    UNION ALL SELECT '2026-04-03'
    UNION ALL SELECT '2026-04-04'
    UNION ALL SELECT '2026-04-05'
    UNION ALL SELECT '2026-04-06'
) d;

-- =========================
-- DEMO QUERIES (VERY USEFUL)
-- =========================

-- View All Attendance (with names)
SELECT s.name, a.date, a.status
FROM attendance a
JOIN students s ON s.id = a.student_id
ORDER BY a.date DESC, s.name;

-- Attendance Percentage per Student
SELECT 
    s.name,
    ROUND(
        COUNT(CASE WHEN a.status='Present' THEN 1 END) * 100.0 / COUNT(*),
        2
    ) AS percentage
FROM attendance a
JOIN students s ON a.student_id = s.id
GROUP BY s.name
ORDER BY percentage DESC;