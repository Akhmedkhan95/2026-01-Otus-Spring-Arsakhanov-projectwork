-- Таблица пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица курсов
CREATE TABLE courses (
                         id BIGSERIAL PRIMARY KEY,
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         teacher_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица уроков
CREATE TABLE lessons (
                         id BIGSERIAL PRIMARY KEY,
                         course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
                         title VARCHAR(200) NOT NULL,
                         content TEXT,
                         order_index INTEGER NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица записей на курсы (ManyToMany)
CREATE TABLE course_enrollments (
                                    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
                                    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (student_id, course_id)
);

-- Таблица прогресса прохождения уроков
CREATE TABLE lesson_progress (
                                 id BIGSERIAL PRIMARY KEY,
                                 student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
                                 completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE(student_id, lesson_id)
);

-- Индексы для производительности
CREATE INDEX idx_courses_teacher ON courses(teacher_id);
CREATE INDEX idx_lessons_course ON lessons(course_id);
CREATE INDEX idx_enrollments_student ON course_enrollments(student_id);
CREATE INDEX idx_progress_student ON lesson_progress(student_id);