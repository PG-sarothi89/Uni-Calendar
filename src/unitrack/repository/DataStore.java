package unitrack.repository;

import unitrack.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Step 12: Central Thread-Safe Data Store
 * Demonstrates Singleton Pattern, thread-safe collections, and serialization.
 * Pre-populates realistic academic seed data and maintains persistence across runs.
 */
public class DataStore {
    private static final String DATA_FILE = "data/unitrack_store.dat";
    private static DataStore instance;

    // Thread-safe in-memory stores
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Course> courses = new ConcurrentHashMap<>();
    private final Map<Integer, CalendarEvent> events = new ConcurrentHashMap<>();
    private final Map<Integer, TodoItem> todoItems = new ConcurrentHashMap<>();

    // ID generators
    private final AtomicInteger nextUserId = new AtomicInteger(100);
    private final AtomicInteger nextCourseId = new AtomicInteger(200);
    private final AtomicInteger nextEventId = new AtomicInteger(300);
    private final AtomicInteger nextTodoId = new AtomicInteger(400);

    private DataStore() {
        if (!loadFromFile()) {
            initSeedData();
            saveToFile();
        }
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initSeedData() {
        // 1. Seed Users (Student, Teacher, Admin)
        Student student = new Student(1, "Alex Morgan", "alex@uni.edu", "pass123");
        Teacher teacher1 = new Teacher(2, "Dr. Sarah Jenkins", "dr.sarah@uni.edu", "pass123");
        Teacher teacher2 = new Teacher(3, "Prof. Alan Turing", "alan.turing@uni.edu", "pass123");
        Admin admin = new Admin(4, "System Administrator", "admin@uni.edu", "admin123");

        users.put(student.getId(), student);
        users.put(teacher1.getId(), teacher1);
        users.put(teacher2.getId(), teacher2);
        users.put(admin.getId(), admin);

        // 2. Seed Academic Courses
        Course oopCourse = new Course(10, "CSE 2102", "Object-Oriented Design & Programming", teacher1.getId(), teacher1.getName(), "Fall 2026");
        oopCourse.addSyllabusTopic(new SyllabusTopic(1, 10, 1, "Introduction to OOP & Class Modeling", true));
        oopCourse.addSyllabusTopic(new SyllabusTopic(2, 10, 2, "Encapsulation, Access Modifiers, and Constructors", true));
        oopCourse.addSyllabusTopic(new SyllabusTopic(3, 10, 3, "Inheritance & Polymorphic Dispatch", true));
        oopCourse.addSyllabusTopic(new SyllabusTopic(4, 10, 4, "Abstract Classes & Interface Contracts", true));
        oopCourse.addSyllabusTopic(new SyllabusTopic(5, 10, 5, "Java Swing GUI & Event Dispatch Thread", true));
        oopCourse.addSyllabusTopic(new SyllabusTopic(6, 10, 6, "Exception Handling & Stream I/O", false));
        oopCourse.addSyllabusTopic(new SyllabusTopic(7, 10, 7, "Generics & Collections Framework", false));
        oopCourse.addSyllabusTopic(new SyllabusTopic(8, 10, 8, "Multithreading & ScheduledExecutorService", false));
        oopCourse.addSyllabusTopic(new SyllabusTopic(9, 10, 9, "Socket Networking & Client-Server Feeds", false));
        oopCourse.addSyllabusTopic(new SyllabusTopic(10, 10, 10, "JDBC & Relational Database Integration", false));

        Course dsaCourse = new Course(11, "CSE 2104", "Data Structures & Algorithms Lab", teacher2.getId(), teacher2.getName(), "Fall 2026");
        dsaCourse.addSyllabusTopic(new SyllabusTopic(11, 11, 1, "Dynamic Arrays & Linked Lists", true));
        dsaCourse.addSyllabusTopic(new SyllabusTopic(12, 11, 2, "Stacks and Queue Structures", true));
        dsaCourse.addSyllabusTopic(new SyllabusTopic(13, 11, 3, "Binary Search Trees & AVL Trees", true));
        dsaCourse.addSyllabusTopic(new SyllabusTopic(14, 11, 4, "Graph Traversals (BFS & DFS)", false));
        dsaCourse.addSyllabusTopic(new SyllabusTopic(15, 11, 5, "Minimum Spanning Tree & Shortest Paths", false));

        Course mathCourse = new Course(12, "MATH 201", "Discrete Mathematics", teacher1.getId(), teacher1.getName(), "Fall 2026");
        mathCourse.addSyllabusTopic(new SyllabusTopic(16, 12, 1, "Propositional Logic & Truth Tables", true));
        mathCourse.addSyllabusTopic(new SyllabusTopic(17, 12, 2, "Set Theory & Relations", true));
        mathCourse.addSyllabusTopic(new SyllabusTopic(18, 12, 3, "Combinatorics & Pigeonhole Principle", false));
        mathCourse.addSyllabusTopic(new SyllabusTopic(19, 12, 4, "Graph Theory Fundamentals", false));

        courses.put(oopCourse.getId(), oopCourse);
        courses.put(dsaCourse.getId(), dsaCourse);
        courses.put(mathCourse.getId(), mathCourse);

        teacher1.assignCourse(oopCourse.getId());
        teacher1.assignCourse(mathCourse.getId());
        teacher2.assignCourse(dsaCourse.getId());

        student.enrollCourse(oopCourse.getId());
        student.enrollCourse(dsaCourse.getId());
        student.enrollCourse(mathCourse.getId());

        // 3. Seed Calendar Events matching both Reference Image and Academic Schedule
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // Notes for the 10th (exactly matching Reference Image schedule)
        addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                "Start with daily Ui",
                "Review desktop UI layouts and interactive calendar components.",
                EventType.LECTURE,
                LocalDate.of(year, month, 10),
                LocalTime.of(8, 30), LocalTime.of(10, 0),
                oopCourse.getId(), teacher1.getId(), "Software Lab 402", "Lecture_UI_Design.pdf"));

        addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                "Meeting with Client",
                "Discussion with course instructor regarding milestone deliverables.",
                EventType.TASK,
                LocalDate.of(year, month, 10),
                LocalTime.of(10, 30), LocalTime.of(11, 45),
                oopCourse.getId(), teacher1.getId(), "Faculty Office 214", null));

        addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                "Lunch with family",
                "Academic midday study break and informal review.",
                EventType.HOLIDAY,
                LocalDate.of(year, month, 10),
                LocalTime.of(13, 0), LocalTime.of(14, 0),
                null, student.getId(), "Campus Cafeteria", null));

        // Event on the 14th (matching the circled date in reference image)
        addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                "MATH 201: Discrete Math Midterm",
                "Combinatorics, sets, relations, and recurrence relations.",
                EventType.EXAM,
                LocalDate.of(year, month, 14),
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                mathCourse.getId(), teacher1.getId(), "Exam Hall B", "Midterm_Topics.pdf"));

        // Event for TODAY (if today is not the 10th or 14th, seed today too)
        if (today.getDayOfMonth() != 10 && today.getDayOfMonth() != 14) {
            addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                    "Start with daily Ui",
                    "Daily sprint check-in and interactive calendar review.",
                    EventType.LECTURE,
                    today,
                    LocalTime.of(8, 30), LocalTime.of(10, 0),
                    oopCourse.getId(), teacher1.getId(), "Room 402", null));

            addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                    "Meeting with Client",
                    "Requirements verification and project roadmap alignment.",
                    EventType.TASK,
                    today,
                    LocalTime.of(10, 30), LocalTime.of(12, 0),
                    oopCourse.getId(), teacher1.getId(), "Room 214", null));

            addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                    "Lunch with family",
                    "Scheduled midday study break.",
                    EventType.HOLIDAY,
                    today,
                    LocalTime.of(13, 0), LocalTime.of(14, 0),
                    null, student.getId(), "Campus Center", null));
        }

        // Additional events throughout month
        addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                "CSE 2102: Swing Architecture",
                "Deep dive into Event Dispatch Thread, painting, and clean components.",
                EventType.LECTURE,
                LocalDate.of(year, month, Math.min(22, today.lengthOfMonth())),
                LocalTime.of(9, 0), LocalTime.of(10, 30),
                oopCourse.getId(), teacher1.getId(), "Room 402", "Swing_EDT.pdf"));

        addEventSeed(new CalendarEvent(nextEventId.getAndIncrement(),
                "University Fall Reading Day",
                "Campus-wide library and study day with no scheduled lectures.",
                EventType.HOLIDAY,
                LocalDate.of(year, month, Math.min(18, today.lengthOfMonth())),
                LocalTime.of(8, 0), LocalTime.of(18, 0),
                null, admin.getId(), "Campus-wide", null));

        // 4. Seed Student To-Do Tasks
        addTodoSeed(new TodoItem(nextTodoId.getAndIncrement(), student.getId(),
                "Complete OODP Project Report & Plan submission", TodoItem.Priority.HIGH, today.plusDays(2), false));
        addTodoSeed(new TodoItem(nextTodoId.getAndIncrement(), student.getId(),
                "Implement Swing CardLayout for Calendar View", TodoItem.Priority.HIGH, today, true));
        addTodoSeed(new TodoItem(nextTodoId.getAndIncrement(), student.getId(),
                "Review Java Concurrency & ScheduledExecutorService", TodoItem.Priority.MEDIUM, today.plusDays(4), false));
        addTodoSeed(new TodoItem(nextTodoId.getAndIncrement(), student.getId(),
                "Read Bloch Effective Java: Chapter on Generics", TodoItem.Priority.LOW, today.plusDays(7), false));
    }

    private void addEventSeed(CalendarEvent event) {
        events.put(event.getId(), event);
    }

    private void addTodoSeed(TodoItem item) {
        todoItems.put(item.getId(), item);
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Map<Integer, Course> getCourses() {
        return courses;
    }

    public Map<Integer, CalendarEvent> getEvents() {
        return events;
    }

    public Map<Integer, TodoItem> getTodoItems() {
        return todoItems;
    }

    public int getNextUserId() {
        return nextUserId.getAndIncrement();
    }

    public int getNextCourseId() {
        return nextCourseId.getAndIncrement();
    }

    public int getNextEventId() {
        return nextEventId.getAndIncrement();
    }

    public int getNextTodoId() {
        return nextTodoId.getAndIncrement();
    }

    public synchronized void saveToFile() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(new ArrayList<>(users.values()));
            oos.writeObject(new ArrayList<>(courses.values()));
            oos.writeObject(new ArrayList<>(events.values()));
            oos.writeObject(new ArrayList<>(todoItems.values()));
        } catch (IOException e) {
            System.err.println("Notice: Could not save data cache to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized boolean loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return false;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<User> userList = (List<User>) ois.readObject();
            List<Course> courseList = (List<Course>) ois.readObject();
            List<CalendarEvent> eventList = (List<CalendarEvent>) ois.readObject();
            List<TodoItem> todoList = (List<TodoItem>) ois.readObject();

            users.clear();
            courses.clear();
            events.clear();
            todoItems.clear();

            for (User u : userList) users.put(u.getId(), u);
            for (Course c : courseList) courses.put(c.getId(), c);
            for (CalendarEvent e : eventList) events.put(e.getId(), e);
            for (TodoItem t : todoList) todoItems.put(t.getId(), t);

            return true;
        } catch (Exception e) {
            System.err.println("Notice: Data file reset required: " + e.getMessage());
            return false;
        }
    }
}
