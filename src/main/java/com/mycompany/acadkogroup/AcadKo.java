/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.acadkogroup;

import java.util.*;

public class AcadKo {

    // ======================= MODELS =======================
    enum Role {
        STUDENT("Student"), REP("Student Representative"), PROFESSOR("Professor"), ADMIN("Administrator");
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

    // ======================= "DATABASE" (MySQL stand-in) =======================
    static class Database {
        List<User> users = new ArrayList<>();
        List<ScheduleEntry> schedules = new ArrayList<>();
        List<Faculty> faculty = new ArrayList<>();
        List<Announcement> announcements = new ArrayList<>();
        List<Report> reports = new ArrayList<>();
        List<Message> messages = new ArrayList<>();
        Map<Integer, List<String>> planner = new HashMap<>(); // userId -> study tasks
        int nextUser = 1, nextSched = 1, nextFac = 1, nextAnn = 1, nextRep = 1;

        Database() { seed(); }

        void seed() {
            users.add(new User(nextUser++, "Admin User", "admin@pup.edu.ph", "admin123", Role.ADMIN, "-"));
            users.add(new User(nextUser++, "Juan Dela Cruz", "juan@pup.edu.ph", "student123", Role.STUDENT, "BSIT 2-1"));
            users.add(new User(nextUser++, "Maria Santos", "maria@pup.edu.ph", "rep123", Role.REP, "BSIT 2-1"));
            users.add(new User(nextUser++, "Prof. Reyes", "reyes@pup.edu.ph", "prof123", Role.PROFESSOR, "-"));

            faculty.add(new Faculty(nextFac++, 4, "Prof. Reyes", "CCIS", "reyes@pup.edu.ph", "Mon/Wed 1-3 PM"));
            faculty.add(new Faculty(nextFac++, 0, "Prof. Garcia", "CCIS", "garcia@pup.edu.ph", "Tue/Thu 9-11 AM"));

            schedules.add(new ScheduleEntry(nextSched++, "BSIT 2-1", "Database Management", "Mon", "8:00-11:00", "Rm 301", "Prof. Reyes"));
            schedules.add(new ScheduleEntry(nextSched++, "BSIT 2-1", "Object-Oriented Programming", "Wed", "1:00-4:00", "Lab 2", "Prof. Reyes"));
            schedules.add(new ScheduleEntry(nextSched++, "BSIT 2-1", "Discrete Mathematics", "Thu", "9:00-12:00", "Rm 205", "Prof. Garcia"));

            announcements.add(new Announcement(nextAnn++, "Welcome to ACADKO! Check your schedules.", "2026-10-01"));
        }

        User login(String email, String password) {           // "Search Users Table"
            for (User u : users)
                if (u.email.equalsIgnoreCase(email) && u.password.equals(password)) return u;
            return null;                                       // "Account Found?" -> No
        }
        User findUser(int id) { for (User u : users) if (u.id == id) return u; return null; }
        ScheduleEntry findSched(int id) { for (ScheduleEntry s : schedules) if (s.id == id) return s; return null; }
        Faculty findFaculty(int id) { for (Faculty f : faculty) if (f.id == id) return f; return null; }
        Faculty facultyOfUser(int userId) { for (Faculty f : faculty) if (f.userId == userId) return f; return null; }
        Report findReport(int id) { for (Report r : reports) if (r.id == id) return r; return null; }
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

    // ======================= MAIN FLOW =======================
    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("  ACADKO - Academic Management System");
        System.out.println("  One platform. One community. A better PUP experience.");
        System.out.println("==============================================================");

        // Start -> Open ACADKO (Login Page) -> Enter credentials -> Account Found?
        User user = null;
        while (user == null) {
            String email = ask("\nEmail / Username (or 'exit'): ");
            if (email.equalsIgnoreCase("exit")) { System.out.println("End."); return; }
            String pass = ask("Password: ");
            user = db.login(email, pass);
            if (user == null) System.out.println("Invalid email or password. Try again.");
        }

        // Get user_id, name, role -> Show Dashboard (role-based menu)
        System.out.println("\nWelcome, " + user.name + " (" + user.role.label + ")!");
        switch (user.role) {
            case STUDENT:   studentMenu(user, false); break;
            case REP:       studentMenu(user, true);  break;
            case PROFESSOR: professorMenu(user);      break;
            case ADMIN:     adminMenu(user);          break;
        }

        // Logout -> End
        System.out.println("\nLogged out. End.");
    }

    // ======================= SHARED FEATURES =======================
    static void viewSection(User u) {
        title("MY SCHEDULE - " + u.section);
        boolean any = false;
        for (ScheduleEntry s : db.schedules)
            if (s.section.equalsIgnoreCase(u.section)) { System.out.println("  " + s); any = true; }
        if (!any) none();
    }

    static void viewAnnouncements() {
        title("ANNOUNCEMENTS");
        if (db.announcements.isEmpty()) none();
        for (Announcement a : db.announcements) System.out.println("  " + a);
    }

    static void viewFaculty() {
        title("FACULTY FINDER");
        if (db.faculty.isEmpty()) none();
        for (Faculty f : db.faculty) System.out.println("  " + f);
    }

    static void messageFaculty(User u) {
        viewFaculty();
        int id = askInt("Faculty # to message (0 to cancel): ");
        if (id == 0) return;
        Faculty f = db.findFaculty(id);
        if (f == null) { System.out.println("  Faculty not found."); return; }
        db.messages.add(new Message(f.id, u.name, ask("Message: ")));
        System.out.println("  Message sent to " + f.name + ".");
    }

    static void studyPlanner(User u) {
        List<String> tasks = db.planner.computeIfAbsent(u.id, k -> new ArrayList<>());
        while (true) {
            title("STUDY PLANNER (modify)");
            if (tasks.isEmpty()) none();
            for (int i = 0; i < tasks.size(); i++) System.out.println("  " + (i + 1) + ". " + tasks.get(i));
            System.out.println("  [1] Add  [2] Edit  [3] Delete  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            if (c == 1) tasks.add(ask("New task: "));
            else if ((c == 2 || c == 3) && !tasks.isEmpty()) {
                int n = askInt("Task number: ");
                if (n < 1 || n > tasks.size()) { System.out.println("  Invalid number."); continue; }
                if (c == 2) tasks.set(n - 1, ask("New text: "));
                else tasks.remove(n - 1);
            }
        }
    }

    /** Report Issue (schedule/faculty). Student -> Rep first; Rep/Professor -> Admin. */
    static void reportIssue(User u) {
        title("REPORT ISSUE");
        System.out.println("  [1] Schedule problem  [2] Faculty problem  [0] Cancel");
        int c = askInt("Choice: ");
        if (c != 1 && c != 2) return; //if (c == 0)
        String desc = ask("Describe the problem: ");
        Report r = new Report(db.nextRep++, u, c == 1 ? "Schedule" : "Faculty", desc);
        db.reports.add(r);                                    // Update in Database
        System.out.println("  Report #" + r.id + " submitted. " +
                (r.status == Status.PENDING_REP ? "Sent to your student representative." : "Sent to admin."));
    }

    static void myReports(User u) {
        title("MY REPORTS");
        boolean any = false;
        for (Report r : db.reports) if (r.reporterId == u.id) { System.out.println("  " + r); any = true; }
        if (!any) none();
    }

    // ======================= STUDENT / STUDENT REP =======================
    static void studentMenu(User u, boolean isRep) {
        while (true) {
            title((isRep ? "STUDENT REPRESENTATIVE" : "STUDENT") + " MENU - " + u.name);
            System.out.println("  1. My Schedule (view)");
            System.out.println("  2. Study Planner (modify)");
            System.out.println(isRep ? "  3. Faculty Finder (view + message)" : "  3. Faculty Finder (view)");
            System.out.println("  4. Announcements (view)");
            System.out.println(isRep ? "  5. Profile (view + forward reports)" : "  5. Profile (report to student rep)");
            if (isRep) {
                System.out.println("  6. Manage Student Requests");
                System.out.println("  7. Report Issue (schedule/faculty)");
            } else {
                System.out.println("  6. Report Issue (schedule/faculty)");
            }
            System.out.println("  0. Logout");
            int c = askInt("Choice: ");

            if (c == 0) return;
            else if (c == 1) viewSection(u);
            else if (c == 2) studyPlanner(u);
            else if (c == 3) { if (isRep) messageFaculty(u); else viewFaculty(); }
            else if (c == 4) viewAnnouncements();
            else if (c == 5) {
                title("PROFILE");
                System.out.println("  " + u);
                if (isRep) forwardReports();
                else {
                    myReports(u);
                    if (ask("Report an issue to your student rep? (y/n): ").equalsIgnoreCase("y")) reportIssue(u);
                }
            }
            else if (isRep && c == 6) manageStudentRequests();
            else if ((isRep && c == 7) || (!isRep && c == 6)) { reportIssue(u); myReports(u); }
            else System.out.println("  Invalid choice.");
        }
    }

    /** Rep: forward pending student reports to admin. */
    static void forwardReports() {
        title("FORWARD REPORTS TO ADMIN");
        List<Report> pending = new ArrayList<>();
        for (Report r : db.reports) if (r.status == Status.PENDING_REP) pending.add(r);
        if (pending.isEmpty()) { none(); return; }
        for (Report r : pending) System.out.println("  " + r);
        int id = askInt("Report # to forward (0 to cancel): ");
        Report r = db.findReport(id);
        if (r != null && r.status == Status.PENDING_REP) {
            r.status = Status.PENDING_ADMIN;                  // Update in Database
            System.out.println("  Report #" + id + " forwarded to admin.");
        } else if (id != 0) System.out.println("  Not a pending report.");
    }

    /** Rep: review student requests, verify, forward, or return for more info. */
    static void manageStudentRequests() {
        while (true) {
            title("MANAGE STUDENT REQUESTS");
            List<Report> pending = new ArrayList<>();
            for (Report r : db.reports) if (r.reporterRole == Role.STUDENT) pending.add(r);
            if (pending.isEmpty()) none();
            for (Report r : pending) System.out.println("  " + r);
            System.out.println("  [1] Verify faculty & forward  [2] Return for more info  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            int id = askInt("Report #: ");
            Report r = db.findReport(id);
            if (r == null || r.reporterRole != Role.STUDENT || r.status != Status.PENDING_REP) {
                System.out.println("  Not a pending student report."); continue;
            }
            if (c == 1) { r.status = Status.PENDING_ADMIN; System.out.println("  Forwarded to admin."); }
            else if (c == 2) { r.status = Status.RETURNED; r.adminNote = ask("Note for student: "); System.out.println("  Returned to student."); }
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

            if (c == 0) return;
            else if (c == 1) {
                title("ASSIGNED CLASSES");
                boolean any = false;
                for (ScheduleEntry s : db.schedules)
                    if (s.professor.equalsIgnoreCase(u.name)) { System.out.println("  " + s); any = true; }
                if (!any) none();
            }
            else if (c == 2) viewFaculty();
            else if (c == 3) editFacultyProfile(u);
            else if (c == 4) viewAnnouncements();
            else if (c == 5) { reportIssue(u); myReports(u); }
            else System.out.println("  Invalid choice.");
        }
    }

    static void editFacultyProfile(User u) {
        Faculty f = db.facultyOfUser(u.id);
        if (f == null) {                                      // create profile on first use
            f = new Faculty(db.nextFac++, u.id, u.name, "", u.email, "");
            db.faculty.add(f);
        }
        title("MY FACULTY PROFILE");
        System.out.println("  " + f);
        int unread = 0;
        for (Message m : db.messages) if (m.toFacultyId == f.id) unread++;
        System.out.println("  Messages received: " + unread);
        for (Message m : db.messages) if (m.toFacultyId == f.id) System.out.println("    - " + m.from + ": " + m.text);
        System.out.println("  (press Enter to keep current value)");
        String d = ask("Department [" + f.department + "]: ");
        String e = ask("Email [" + f.email + "]: ");
        String cs = ask("Consultation hours [" + f.consultation + "]: ");
        if (!d.isEmpty()) f.department = d;
        if (!e.isEmpty()) f.email = e;
        if (!cs.isEmpty()) f.consultation = cs;
        System.out.println("  Profile updated.");               // Update in Database
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
            else if (c == 4) announcementMgmt();
            else if (c == 5) reportsMgmt();
            else System.out.println("  Invalid choice.");
        }
    }

    static void scheduleMgmt() {
        while (true) {
            title("SCHEDULE MANAGEMENT");
            for (ScheduleEntry s : db.schedules) System.out.println("  " + s);
            System.out.println("  [1] Upload/Add  [2] Update  [3] Delete  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            if (c == 1) {
                db.schedules.add(new ScheduleEntry(db.nextSched++, ask("Section: "), ask("Subject: "),
                        ask("Day: "), ask("Time: "), ask("Room: "), ask("Professor: ")));
                System.out.println("  Schedule added.");
            } else if (c == 2 || c == 3) {
                ScheduleEntry s = db.findSched(askInt("Schedule #: "));
                if (s == null) { System.out.println("  Not found."); continue; }
                if (c == 3) { db.schedules.remove(s); System.out.println("  Deleted."); }
                else {
                    System.out.println("  (press Enter to keep current value)");
                    String v;
                    if (!(v = ask("Section [" + s.section + "]: ")).isEmpty()) s.section = v;
                    if (!(v = ask("Subject [" + s.subject + "]: ")).isEmpty()) s.subject = v;
                    if (!(v = ask("Day [" + s.day + "]: ")).isEmpty()) s.day = v;
                    if (!(v = ask("Time [" + s.time + "]: ")).isEmpty()) s.time = v;
                    if (!(v = ask("Room [" + s.room + "]: ")).isEmpty()) s.room = v;
                    if (!(v = ask("Professor [" + s.professor + "]: ")).isEmpty()) s.professor = v;
                    System.out.println("  Updated.");
                }
            }
        }
    }

    static void facultyMgmt() {
        while (true) {
            title("FACULTY MANAGEMENT");
            for (Faculty f : db.faculty) System.out.println("  " + f);
            System.out.println("  [1] Add  [2] Update  [3] Delete  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            if (c == 1) {
                db.faculty.add(new Faculty(db.nextFac++, 0, ask("Name: "), ask("Department: "),
                        ask("Email: "), ask("Consultation hours: ")));
                System.out.println("  Faculty added.");
            } else if (c == 2 || c == 3) {
                Faculty f = db.findFaculty(askInt("Faculty #: "));
                if (f == null) { System.out.println("  Not found."); continue; }
                if (c == 3) { db.faculty.remove(f); System.out.println("  Deleted."); }
                else {
                    System.out.println("  (press Enter to keep current value)");
                    String v;
                    if (!(v = ask("Name [" + f.name + "]: ")).isEmpty()) f.name = v;
                    if (!(v = ask("Department [" + f.department + "]: ")).isEmpty()) f.department = v;
                    if (!(v = ask("Email [" + f.email + "]: ")).isEmpty()) f.email = v;
                    if (!(v = ask("Consultation [" + f.consultation + "]: ")).isEmpty()) f.consultation = v;
                    System.out.println("  Updated.");
                }
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
            for (User u : db.users) System.out.println("  " + u);
            System.out.println("  [1] Add  [2] Update  [3] Delete  [4] Change Role  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            if (c == 1) {
                String name = ask("Name: "), email = ask("Email: "), pw = ask("Password: ");
                Role r = askRole();
                if (r == null) { System.out.println("  Invalid role."); continue; }
                boolean dup = false;
                for (User u : db.users) if (u.email.equalsIgnoreCase(email)) dup = true;
                if (dup) { System.out.println("  Email already exists."); continue; }
                db.users.add(new User(db.nextUser++, name, email, pw, r, ask("Section (or -): ")));
                System.out.println("  User added.");
            } else if (c >= 2 && c <= 4) {
                User u = db.findUser(askInt("User #: "));
                if (u == null) { System.out.println("  Not found."); continue; }
                if (c == 3) {
                    if (u.id == me.id) { System.out.println("  You cannot delete your own account."); continue; }
                    db.users.remove(u); System.out.println("  Deleted.");
                } else if (c == 4) {
                    Role r = askRole();
                    if (r == null) { System.out.println("  Invalid role."); continue; }
                    u.role = r; System.out.println("  Role changed to " + r.label + ".");
                } else {
                    System.out.println("  (press Enter to keep current value)");
                    String v;
                    if (!(v = ask("Name [" + u.name + "]: ")).isEmpty()) u.name = v;
                    if (!(v = ask("Email [" + u.email + "]: ")).isEmpty()) u.email = v;
                    if (!(v = ask("Password [hidden]: ")).isEmpty()) u.password = v;
                    if (!(v = ask("Section [" + u.section + "]: ")).isEmpty()) u.section = v;
                    System.out.println("  Updated.");
                }
            }
        }
    }

    static void announcementMgmt() {
        while (true) {
            title("ANNOUNCEMENT MANAGEMENT");
            if (db.announcements.isEmpty()) none();
            for (Announcement a : db.announcements) System.out.println("  " + a);
            System.out.println("  [1] Add  [2] Delete  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            if (c == 1) {
                db.announcements.add(new Announcement(db.nextAnn++, ask("Announcement: "), ask("Date (YYYY-MM-DD): ")));
                System.out.println("  Posted.");
            } else if (c == 2) {
                int id = askInt("Announcement #: ");
                boolean removed = db.announcements.removeIf(a -> a.id == id);
                System.out.println(removed ? "  Deleted." : "  Not found.");
            }
        }
    }

    /** Reporting flow (admin side): View -> Review/Modify -> Resolve? Yes: Mark Resolved / No: Reject-Return. */
    static void reportsMgmt() {
        while (true) {
            title("REPORTS MANAGEMENT");
            boolean any = false;
            for (Report r : db.reports)
                if (r.status == Status.PENDING_ADMIN || r.status == Status.RESOLVED || r.status == Status.RETURNED) {
                    System.out.println("  " + r); any = true;
                }
            if (!any) none();
            System.out.println("  [1] Review/Modify  [2] Resolve  [3] Reject / Return for more info  [0] Back");
            int c = askInt("Choice: ");
            if (c == 0) return;
            Report r = db.findReport(askInt("Report #: "));
            if (r == null || r.status == Status.PENDING_REP) {
                System.out.println("  Not available (not yet forwarded to admin, or not found)."); continue;
            }
            if (c == 1) {
                System.out.println("  " + r);
                String v = ask("Edit description (Enter to keep): ");
                if (!v.isEmpty()) r.description = v;
                v = ask("Admin note (Enter to keep): ");
                if (!v.isEmpty()) r.adminNote = v;
            } else if (c == 2) {
                r.status = Status.RESOLVED;                   // Mark as Resolved -> Update in Database
                System.out.println("  Marked as resolved.");
            } else if (c == 3) {
                r.status = Status.RETURNED;                   // Reject / Return for more info
                r.adminNote = ask("Reason / info needed: ");
                System.out.println("  Returned to reporter.");
            }
        }
    }
}

