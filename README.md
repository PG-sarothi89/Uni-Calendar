# UniTrack — Smart Academic Calendar

A modern desktop academic calendar and schedule management system built in **Java (Swing / AWT)** adhering strictly to **Object-Oriented Design & Programming (OODP)** principles.

---

## 🌟 Key Features

- **Split-Card Interactive Calendar**:
  - Left panel: Dark low-poly faceted background with vertical month roller, date banner, and colored timestamp schedule notes (`08:30`, `10:30`, `13:00`).
  - Right panel: Clean numeric date grid with hot-pink underline on the current date, hot-pink circular ring on the selected date, event dots, and bottom status/weather widget (`Clouds & Sunshine`, `26°C`, `Rochester NY`).
- **Role-Based Access Control (RBAC)**:
  - **Student**: View enrolled course schedules, download lecture slides, manage private study to-dos.
  - **Teacher**: Publish lectures, quizzes, and exams; attach PDF materials; mark syllabus topic completion.
  - **Administrator**: Manage institutional holiday feeds, semester bounds, and broadcast announcements.
- **Curriculum & Syllabus Progress Tracker**:
  - Real-time `JProgressBar` tracking course progress percentage based on completed curriculum topics.
- **Personal Study Checklist**:
  - Independent task manager with priorities (`HIGH`, `MEDIUM`, `LOW`) and deadlines.
- **Multithreaded Reminder Daemon**:
  - Background `ScheduledExecutorService` running every 60s to monitor approaching deadlines and safely dispatch alerts to the Swing Event Dispatch Thread (EDT).
- **Socket Networking Subsystem**:
  - Client-server TCP socket protocol for synchronizing campus-wide holiday notices.
- **Normalized Persistence**:
  - 3NF relational MySQL schema (`schema.sql`) and a built-in zero-dependency thread-safe file persistence store (`DataStore.java`).

---

## 🚀 Getting Started

### Prerequisites
- **Java SE 17+** (JDK 21 or 25 LTS recommended). Verify with:
  ```bash
  java -version
  javac -version
  ```

### Build & Run
- **Windows**:
  - Double-click `build.bat` to compile.
  - Double-click `run.bat` to launch.
- **Terminal (Cross-Platform)**:
  ```bash
  # Compile
  javac -encoding UTF-8 -d out -sourcepath src src/MainApp.java

  # Run
  java -cp out MainApp
  ```

---

## 🔑 Demo Accounts

Use the convenient 1-click login buttons on the login card:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Student** | `alex@uni.edu` | `pass123` | View personal calendar, syllabus & private tasks |
| **Teacher** | `dr.sarah@uni.edu` | `pass123` | Create/edit events, check off syllabus topics |
| **Administrator** | `admin@uni.edu` | `admin123` | Full administrative privileges, sync holiday feed |

---

## 🏗️ Project Architecture

```
UNI Calendar/
├── build.bat                                  # 1-click clean compilation script
├── run.bat                                    # 1-click execution script
├── schema.sql                                 # Relational database schema (3NF)
├── src/
│   ├── MainApp.java                           # EDT-safe Swing bootstrap entry point
│   └── unitrack/
│       ├── model/                             # Domain models (User, Student, Teacher, CalendarEvent, Course, etc.)
│       ├── repository/                        # Generic Repository<T, ID> pattern & DataStore
│       ├── service/                           # Business logic & RBAC write authorization
│       ├── concurrency/                       # Background reminder daemon (ScheduledExecutorService)
│       ├── networking/                        # TCP ServerSocket daemon & sync client
│       ├── util/                              # Date arithmetic, UI fonts & vector graphics
│       └── view/                              # Modern Swing UI cards, components & dialogs
```

---

## 📄 License
Academic Project Submission for Object-Oriented Design and Programming (CSE 2102).
