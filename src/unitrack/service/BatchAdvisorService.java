package unitrack.service;

import unitrack.model.*;
import unitrack.repository.CourseRepository;
import unitrack.repository.DataStore;
import unitrack.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Step 21: Batch Advisor Service
 * Exclusively provides administrative capabilities for the Batch Advisor:
 *   1. Registering new faculty members (Teachers) with department assignments.
 *   2. Assigning dedicated courses and automatically scheduling weekly recurring classes on the calendar.
 */
public class BatchAdvisorService {
    private static BatchAdvisorService instance;
    private final UserRepository userRepository = new UserRepository();
    private final CourseRepository courseRepository = new CourseRepository();

    private BatchAdvisorService() {}

    public static synchronized BatchAdvisorService getInstance() {
        if (instance == null) {
            instance = new BatchAdvisorService();
        }
        return instance;
    }

    /**
     * Retrieves all registered teachers/professors
     */
    public List<Teacher> getAllTeachers() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .collect(Collectors.toList());
    }

    /**
     * Registers a new teacher under the authority of the Batch Advisor
     */
    public Teacher addNewTeacher(String name, String email, String password, String department, User activeUser) {
        if (activeUser == null || !activeUser.isBatchAdvisor()) {
            throw new SecurityException("Access Denied: Only the Batch Advisor can register faculty members.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty name cannot be empty.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid university email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("A user with this email address already exists.");
        }

        Teacher teacher = new Teacher(0, name.trim(), email.trim().toLowerCase(), password.trim(), department);
        userRepository.save(teacher);
        return teacher;
    }

    /**
     * Adds a dedicated course and schedules weekly recurring class sessions for the teacher
     */
    public int addDedicatedClass(int teacherId, String courseCode, String courseTitle,
                                 String semester, DayOfWeek dayOfWeek,
                                 LocalTime startTime, LocalTime endTime,
                                 String room, User activeUser) {
        if (activeUser == null || !activeUser.isBatchAdvisor()) {
            throw new SecurityException("Access Denied: Only the Batch Advisor can schedule dedicated classes.");
        }

        Optional<User> userOpt = userRepository.findById(teacherId);
        if (!userOpt.isPresent() || !(userOpt.get() instanceof Teacher)) {
            throw new IllegalArgumentException("Invalid Teacher ID specified.");
        }
        Teacher teacher = (Teacher) userOpt.get();

        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }
        if (courseTitle == null || courseTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty.");
        }
        if (startTime == null || endTime == null || endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Valid start and end times must be specified.");
        }

        String finalSemester = (semester != null && !semester.trim().isEmpty()) ? semester.trim() : "Fall 2026";
        String finalRoom = (room != null && !room.trim().isEmpty()) ? room.trim() : "Audi 1";

        // Find or create course
        Optional<Course> existingCourseOpt = courseRepository.findAll().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(courseCode.trim()))
                .findFirst();

        Course course;
        if (existingCourseOpt.isPresent()) {
            course = existingCourseOpt.get();
            course.setInstructorId(teacher.getId());
            course.setInstructorName(teacher.getName());
            course.setSemester(finalSemester);
            courseRepository.update(course);
        } else {
            course = new Course(0, courseCode.trim().toUpperCase(), courseTitle.trim(),
                    teacher.getId(), teacher.getName(), finalSemester);
            // Add initial syllabus blueprint topics
            course.addSyllabusTopic(new SyllabusTopic(1, 0, 1, "Course Introduction & Objectives", false));
            course.addSyllabusTopic(new SyllabusTopic(2, 0, 2, "Core Foundations & Fundamental Concepts", false));
            course.addSyllabusTopic(new SyllabusTopic(3, 0, 3, "Advanced Architecture & System Analysis", false));
            course.addSyllabusTopic(new SyllabusTopic(4, 0, 4, "Midterm Examination Review", false));
            course.addSyllabusTopic(new SyllabusTopic(5, 0, 5, "Applied Project Architecture & Final Review", false));
            courseRepository.save(course);
        }

        teacher.assignCourse(course.getId());
        userRepository.update(teacher);

        // Schedule weekly recurring lectures for the next 8 weeks matching dayOfWeek
        LocalDate start = LocalDate.now();
        int lecturesScheduled = 0;

        for (int i = 0; i < 60; i++) {
            LocalDate d = start.plusDays(i);
            if (d.getDayOfWeek() == dayOfWeek) {
                int eventId = DataStore.getInstance().getNextEventId();
                CalendarEvent lectureEvent = new CalendarEvent(
                        eventId,
                        course.getCode() + ": " + course.getTitle(),
                        "Dedicated lecture instructed by " + teacher.getName() + " in " + finalRoom + ".",
                        EventType.LECTURE,
                        d,
                        startTime,
                        endTime,
                        course.getId(),
                        teacher.getId(),
                        finalRoom,
                        null
                );
                DataStore.getInstance().getEvents().put(lectureEvent.getId(), lectureEvent);
                lecturesScheduled++;
            }
        }

        DataStore.getInstance().saveToFile();
        return lecturesScheduled;
    }
}
