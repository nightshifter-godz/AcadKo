/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.acadkogroup;

import java.util.*;

public class AcadKo {

    // ======================= MODELS =======================
    enum Role {
        STUDENT("Student"), REP("Student R6"
                + "Representative"), PROFESSOR("Professor"), ADMIN("Administrator");
        final String label;
        Role(String label) { this.label = label; }
    }

    enum Status { PENDING_REP, PENDING_ADMIN, RETURNED, RESOLVED }

    static class User {
        int id; String name, email, password, section; Role role;
        User(int id, String name, String email, String password, Role role, String section) {
            this.id = id; this.name = name; this.email = email;
            this.password = password; this.role = role; this.section = section;
        }
        public String toString() {
            return String.format("#%d | %-22s | %-24s | %-22s | %s", id, name, email, role.label, section);
        }
    }

    static class ScheduleEntry {
        int id; String section, subject, day, time, room, professor;
        ScheduleEntry(int id, String section, String subject, String day, String time, String room, String professor) {
            this.id = id; this.section = section; this.subject = subject;
            this.day = day; this.time = time; this.room = room; this.professor = professor;
        }
        public String toString() {
            return String.format("#%d | %-8s | %-26s | %-4s %-12s | %-6s | %s",
                    id, section, subject, day, time, room, professor);
        }
    }

    static class Faculty {
        int id; int userId; String name, department, email, consultation;
        Faculty(int id, int userId, String name, String department, String email, String consultation) {
            this.id = id; this.userId = userId; this.name = name;
            this.department = department; this.email = email; this.consultation = consultation;
        }
        public String toString() {
            return String.format("#%d | %-20s | %-12s | %-24s | Consultation: %s",
                    id, name, department, email, consultation);
        }
    }

    static class Announcement {
        int id; String text; String date;
        Announcement(int id, String text, String date) { this.id = id; this.text = text; this.date = date; }
        public String toString() { return String.format("#%d [%s] %s", id, date, text); }
    }

    static class Report {
        int id; int reporterId; String reporterName; Role reporterRole;
        String type; // "Schedule" or "Faculty"
        String description; Status status; String adminNote = "";
        Report(int id, User u, String type, String description) {
            this.id = id; this.reporterId = u.id; this.reporterName = u.name;
            this.reporterRole = u.role; this.type = type; this.description = description;
            // Reporting flow: students go to their Rep first; faculty/rep go straight to Admin
            this.status = (u.role == Role.STUDENT) ? Status.PENDING_REP : Status.PENDING_ADMIN;
        }
        public String toString() {
            return String.format("#%d | %-8s | by %s (%s) | %s | Status: %s%s",
                    id, type, reporterName, reporterRole.label, description, status,
                    adminNote.isEmpty() ? "" : " | Note: " + adminNote);
        }
    }

    static class Message {
        int toFacultyId; String from, text;
        Message(int toFacultyId, String from, String text) { this.toFacultyId = toFacultyId; this.from = from; this.text = text; }
    }


    // ======================= INPUT HELPERS =======================
    static final Scanner in = new Scanner(System.in);
    static final Database db = new Database();

    static String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    static int askInt(String prompt) {
        while (true) {
            String s = ask(prompt);
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("  Please enter a number."); }
        }
    }

    static void line() { System.out.println("--------------------------------------------------------------"); }
    static void title(String t) { System.out.println(); line(); System.out.println("  " + t); line(); }
    static void none() { System.out.println("  (nothing to show)"); }
        
    
              //////=============MAIN FLOW=================//////

    public static void main(String[] args) {

    Database db = new Database();
    Scanner sc = new Scanner(System.in);

    System.out.println("======================================");
    System.out.println("             ACADKO");
    System.out.println("   Academic Management System");
    System.out.println("======================================");

    while (true) {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        User user = db.login(email, password);

        if (user == null) {
            System.out.println("\nInvalid email or password.");
            System.out.println("Please try again.");
            continue;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.name + "!");
        System.out.println("Role: " + user.role.label);

        switch (user.role) {

            case STUDENT:
                studentMenu(user, false);
                break;

            case REP:
                studentMenu(user, true);
                break;

            case PROFESSOR:
                professorMenu(user);
                break;

            case ADMIN:
                adminMenu(user);
                break;
        }

        System.out.println("\nYou have been logged out.");
    }
    }
    // ======================= SHARED FEATURES =======================      title("MY SCHEDULE - " + u.section);

    static void viewSection(User u) {
      title("MY SCHEDULE");

      System.out.println();
      System.out.println("  Student Name : " + u.name);
      System.out.println("  Block Section: " + u.section);

      System.out.println();
      System.out.println("==============================================================");

      db.viewSchedule(u.section);

        
}

    static void viewAnnouncements() {
        title("ANNOUNCEMENTS");
        db.viewAnnouncements();
}



    static void viewFaculty() {
        title("FACULTY FINDER");
        db.viewFaculty();
    }


    static void messageFaculty(User u) {

    viewFaculty();

    int facultyId = askInt("Faculty # to message (0 to cancel): ");

    if (facultyId == 0) {
        return;
    }

    String message = ask("Message: ");

    if (message.isEmpty()) {
        System.out.println("  Message cannot be empty.");
        return;
    }

    int receiverId = db.getFacultyUserId(facultyId);

    if (receiverId == -1) {
        System.out.println("  This faculty member does not have a registered account yet.");
        return;
    }

    db.sendMessage(u.id, receiverId, message);
}



static void studyPlanner(User u) {
    while (true) {
        title("STUDY PLANNER");

        System.out.println("  [1] Add Task");
        System.out.println("  [2] View Tasks");
        System.out.println("  [3] Edit Task");
        System.out.println("  [4] Delete Task");
        System.out.println("  [5] Mark Task as Complete");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;
        }

        else if (c == 1) {
            System.out.println("\n  ADD STUDY TASK");

            String task = ask("Task description: ");
            String dueDate = ask("Due date (YYYY-MM-DD, blank if none): ");

            if (task.isEmpty()) {
                System.out.println("  Task description cannot be empty.");
                continue;
            }

            db.addStudyTask(u.id, task, dueDate);
        }

        else if (c == 2) {
            db.viewStudyTasks(u.id);
        }

        else if (c == 3) {
            System.out.println("\n  EDIT STUDY TASK");

            int taskId = askInt("Task ID to edit: ");
            String task = ask("New task description: ");
            String dueDate = ask("New due date (YYYY-MM-DD, blank if none): ");

            if (task.isEmpty()) {
                System.out.println("  Task description cannot be empty.");
                continue;
            }

            db.updateStudyTask(taskId, u.id, task, dueDate);
        }

        else if (c == 4) {
            System.out.println("\n  DELETE STUDY TASK");

            int taskId = askInt("Task ID to delete: ");
            db.deleteStudyTask(taskId, u.id);
        }

        else if (c == 5) {
            System.out.println("\n  MARK TASK AS COMPLETE");

            int taskId = askInt("Task ID to complete: ");
            db.completeStudyTask(taskId, u.id);
        }

        else {
            System.out.println("  Invalid choice.");
        }

        System.out.println("\n  Press Enter to continue...");
        ask("");
    }
}

    /** Report Issue (schedule/faculty). Student -> Rep first; Rep/Professor -> Admin. */
    static void reportIssue(User u) {

    title("REPORT ISSUE");

    System.out.println("  [1] Schedule problem");
    System.out.println("  [2] Faculty problem");
    System.out.println("  [0] Cancel");

    int c = askInt("Choice: ");

    if (c == 0) {
        return;
    }

    if (c != 1 && c != 2) {
        System.out.println("  Invalid choice.");
        return;
    }

    String subject;

    if (c == 1) {
        subject = "Schedule";
    } else {
        subject = "Faculty";
    }

    String description = ask("Describe the problem: ");

    if (description.isEmpty()) {
        System.out.println("  Description cannot be empty.");
        return;
    }

    db.submitReport(u.id, subject, description);
}

    static void myReports(User u) {

    title("MY REPORTS");

    db.viewMyReports(u.id);
}


    // ======================= STUDENT / STUDENT REP =======================
    static void studentMenu(User u, boolean isRep) {
    while (true) {

        title((isRep ? "STUDENT REPRESENTATIVE" : "STUDENT") + " MENU - " + u.name);

        System.out.println("  1. My Schedule (view)");
        System.out.println("  2. Study Planner (modify)");

        if (isRep) {
            System.out.println("  3. Faculty Finder (view + message)");
        } else {
            System.out.println("  3. Faculty Finder (view)");
        }

        System.out.println("  4. Announcements (view)");
        System.out.println("  5. Profile (view)");

        if (isRep) {
            System.out.println("  6. Manage Student Reports");
            System.out.println("  7. Report Issue (schedule/faculty)");
        } else {
            System.out.println("  6. Report Issue (schedule/faculty)");
        }

        System.out.println("  0. Logout");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;

        } else if (c == 1) {
            viewSection(u);

        } else if (c == 2) {
            studyPlanner(u);

        } else if (c == 3) {
            if (isRep) {
                messageFaculty(u);
            } else {
                viewFaculty();
            }

        } else if (c == 4) {
            viewAnnouncements();

        } else if (c == 5) {
            title("PROFILE");
            System.out.println("  " + u);

        } else if (isRep && c == 6) {
            manageStudentReports();

        } else if ((isRep && c == 7) || (!isRep && c == 6)) {
            reportIssue(u);
            myReports(u);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}

    /** Rep: forward pending student reports to admin. */

    static void manageStudentReports() {

    while (true) {

        title("MANAGE STUDENT REPORTS");

        db.viewPendingStudentReports();

        System.out.println();
        System.out.println("  [1] Forward Report to Admin");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;
        }

        if (c == 1) {

            int reportId = askInt("Report ID to forward: ");

            db.forwardReport(reportId);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}

    // ======================= PROFESSOR =======================
    static void professorMenu(User u) {

    while (true) {

        title("PROFESSOR MENU - " + u.name);

        System.out.println("  1. My Schedule (view)");
        System.out.println("  2. Faculty Finder (view)");
        System.out.println("  3. My Faculty Profile (modify)");
        System.out.println("  4. Announcements (view)");
        System.out.println("  5. Report Issue (schedule/faculty)");
        System.out.println("  0. Logout");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;
        }

        else if (c == 1) {
        title("MY SCHEDULE");
            db.viewFacultySchedule(u.id);

        }

        else if (c == 2) {
            viewFaculty();
        }

        else if (c == 3) {
            editFacultyProfile(u);
        }

        else if (c == 4) {
            viewAnnouncements();
        }

        else if (c == 5) {
            reportIssue(u);
            myReports(u);
        }

              else {
            System.out.println("  Invalid choice.");
        }
    }
}

    static void editFacultyProfile(User u) {

    title("MY FACULTY PROFILE");

    db.viewMyFacultyProfile(u.id);

    System.out.println();
    System.out.println("  Faculty profile is currently view-only.");
    System.out.println("  Profile editing will be connected to MySQL next.");
}


    // ======================= ADMIN =======================
    static void adminMenu(User u) {
        while (true) {
            title("ADMIN DASHBOARD - " + u.name);
            System.out.println("  1. Schedule Management");
            System.out.println("  2. Faculty Management");
            System.out.println("  3. User Management");
            System.out.println("  4. Announcement Management");
            System.out.println("  5. Reports Management");
            System.out.println("  0. Logout");
            int c = askInt("Choice: ");
            if (c == 0) return;
            else if (c == 1) scheduleMgmt();
            else if (c == 2) facultyMgmt();
            else if (c == 3) userMgmt(u);
            else if (c == 4) announcementMgmt(u);
            else if (c == 5) reportsMgmt();
            else System.out.println("  Invalid choice.");
        }
    }


    static void scheduleMgmt() {
    while (true) {
        title("SCHEDULE MANAGEMENT");

        db.viewAllSchedules();

        System.out.println();
        System.out.println("  [1] Add Schedule");
        System.out.println("  [2] Update Schedule");
        System.out.println("  [3] Delete Schedule");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;

        } else if (c == 1) {
            System.out.println("\n  ADD SCHEDULE");

            String day = ask("Day: ");
            int subjectId = askInt("Subject ID: ");
            int facultyId = askInt("Faculty ID: ");
            String mode = ask("Mode (FTF/Online): ");
            String room = ask("Room: ");
            String startTime = ask("Start time (HH:MM:SS): ");
            String endTime = ask("End time (HH:MM:SS): ");
            int blockId = askInt("Block ID: ");

            db.addSchedule(
                day,
                subjectId,
                facultyId,
                mode,
                room,
                startTime,
                endTime,
                blockId
            );

        } else if (c == 2) {
            int scheduleId = askInt("Schedule ID to update: ");

            String day = ask("New day: ");
            int subjectId = askInt("New Subject ID: ");
            int facultyId = askInt("New Faculty ID: ");
            String mode = ask("New mode (FTF/Online): ");
            String room = ask("New room: ");
            String startTime = ask("New start time (HH:MM:SS): ");
            String endTime = ask("New end time (HH:MM:SS): ");
            int blockId = askInt("New Block ID: ");

            db.updateSchedule(
                scheduleId,
                day,
                subjectId,
                facultyId,
                mode,
                room,
                startTime,
                endTime,
                blockId
            );

        } else if (c == 3) {
            int scheduleId = askInt("Schedule ID to delete: ");
            db.deleteSchedule(scheduleId);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}
    
    static void facultyMgmt() {
    while (true) {
        title("FACULTY MANAGEMENT");

        db.viewFaculty();

        System.out.println();
        System.out.println("  [1] Add Faculty");
        System.out.println("  [2] Update Faculty");
        System.out.println("  [3] Delete Faculty");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;

        } else if (c == 1) {
            System.out.println("\n  ADD FACULTY");

            String name = ask("Name: ");
            String rank = ask("Faculty Rank: ");
            String department = ask("Department: ");
            String email = ask("Email: ");
            String medium = ask("Preferred Medium: ");

            db.addFaculty(name, rank, department, email, medium);

        } else if (c == 2) {
            int facultyId = askInt("Faculty ID to update: ");

            String name = ask("New name: ");
            String rank = ask("New faculty rank: ");
            String department = ask("New department: ");
            String email = ask("New email: ");
            String medium = ask("New preferred medium: ");

            db.updateFaculty(
                facultyId,
                name,
                rank,
                department,
                email,
                medium
            );

        } else if (c == 3) {
            int facultyId = askInt("Faculty ID to delete: ");
            db.deleteFaculty(facultyId);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}

    static Role askRole() {
        System.out.println("  Roles: 1=Student 2=Student Rep 3=Professor 4=Admin");
        switch (askInt("Role: ")) {
            case 1: return Role.STUDENT;
            case 2: return Role.REP;
            case 3: return Role.PROFESSOR;
            case 4: return Role.ADMIN;
            default: return null;
        }
    }

    static void userMgmt(User me) {
    while (true) {
        title("USER MANAGEMENT");

        db.viewUsers();

        System.out.println();
        System.out.println("  [1] Add User");
        System.out.println("  [2] Update User");
        System.out.println("  [3] Delete User");
        System.out.println("  [4] Change Role");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;

        } else if (c == 1) {
            System.out.println("\n  ADD USER");

            String name = ask("Name: ");
            String email = ask("Email: ");
            String password = ask("Password: ");
            Role role = askRole();

            if (role == null) {
                System.out.println("  Invalid role.");
                continue;
            }

            db.addUser(
                name,
                email,
                password,
                role
            );

        } else if (c == 2) {
            int userId = askInt("User ID to update: ");

            String name = ask("New name: ");
            String email = ask("New email: ");
            String password = ask("New password: ");

            db.updateUser(
                userId,
                name,
                email,
                password
            );

        } else if (c == 3) {
            int userId = askInt("User ID to delete: ");

            if (userId == me.id) {
                System.out.println("  You cannot delete your own account.");
                continue;
            }

            db.deleteUser(userId);

        } else if (c == 4) {
            int userId = askInt("User ID to change role: ");

            Role role = askRole();

            if (role == null) {
                System.out.println("  Invalid role.");
                continue;
            }

            db.changeUserRole(userId, role);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}

    static void announcementMgmt(User me) {
    while (true) {
        title("ANNOUNCEMENT MANAGEMENT");

        db.viewAnnouncements();

        System.out.println();
        System.out.println("  [1] Add Announcement");
        System.out.println("  [2] Delete Announcement");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;

        } else if (c == 1) {
            System.out.println("\n  ADD ANNOUNCEMENT");

            String title = ask("Title: ");
            String content = ask("Content: ");

            db.addAnnouncement(
                me.id,
                title,
                content
            );

        } else if (c == 2) {
            int announcementId = askInt("Announcement ID to delete: ");

            db.deleteAnnouncement(announcementId);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}    
    /** Reporting flow (admin side): View -> Review/Modify -> Resolve? Yes: Mark Resolved / No: Reject-Return. */
    static void reportsMgmt() {
    while (true) {
        title("REPORTS MANAGEMENT");

        db.viewForwardedReports();

        System.out.println();
        System.out.println("  [1] Review Report");
        System.out.println("  [2] Mark Resolved");
        System.out.println("  [0] Back");

        int c = askInt("Choice: ");

        if (c == 0) {
            return;

        } else if (c == 1) {

            int reportId = askInt("Report ID: ");

            System.out.println();
            db.viewReport(reportId);

        } else if (c == 2) {

            int reportId = askInt("Report ID to resolve: ");

            db.resolveReport(reportId);

        } else {
            System.out.println("  Invalid choice.");
        }
    }
}
}