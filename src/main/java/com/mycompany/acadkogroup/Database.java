/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.acadkogroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private final String URL = "jdbc:mysql://localhost:3306/AcadKo";
    private final String USERNAME = "root";
    private final String PASSWORD = "AcadKo12345";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }


// Login
    public AcadKo.User login(String email, String password) {

    String sql = """
        SELECT
            u.user_Id,
            u.name,
            u.email,
            u.password,
            u.role,
            b.block_name
        FROM users u
        LEFT JOIN students s
            ON u.user_Id = s.user_id
        LEFT JOIN blocks b
            ON s.block_id = b.block_id
        WHERE u.email = ?
          AND u.password = ?
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {

            int id = rs.getInt("user_Id");
            String name = rs.getString("name");
            String userEmail = rs.getString("email");
            String userPassword = rs.getString("password");
            String roleText = rs.getString("role");
            String section = rs.getString("block_name");

            if (section == null) {
                section = "-";
            }

            AcadKo.Role role;

            switch (roleText) {

                case "student":
                    role = AcadKo.Role.STUDENT;
                    break;

                case "student representative":
                    role = AcadKo.Role.REP;
                    break;

                case "professor":
                    role = AcadKo.Role.PROFESSOR;
                    break;

                case "admin":
                    role = AcadKo.Role.ADMIN;
                    break;

                default:
                    System.out.println("Unknown role in the database.");
                    return null;
            }

            return new AcadKo.User(
                id,
                name,
                userEmail,
                userPassword,
                role,
                section
            );
        }

    } catch (SQLException e) {

        System.out.println("Database connection error.");
        System.out.println(e.getMessage());
    }

    return null;
}
              ///SCHEDULE///

        public void viewSchedule(String section) {

    String sql = """
        SELECT
            b.block_name,
            sub.subject_code,
            sub.subject_name,
            f.name AS faculty_name,
            s.day,
            s.start_time,
            s.end_time,
            s.mode,
            s.room
        FROM schedules s
        JOIN blocks b
            ON s.block_id = b.block_id
        JOIN subjects sub
            ON s.subject_Id = sub.subject_Id
        JOIN faculty f
            ON s.faculty_Id = f.faculty_id
        WHERE b.block_name = ?
        ORDER BY
            FIELD(s.day, 'Monday', 'Tuesday', 'Wednesday',
                  'Thursday', 'Friday', 'Saturday', 'Sunday'),
            s.start_time
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setString(1, section);

        ResultSet rs = stmt.executeQuery();

        boolean any = false;

        while (rs.next()) {

            any = true;

            String day = rs.getString("day");
            String start = rs.getString("start_time");
            String end = rs.getString("end_time");
            String mode = rs.getString("mode");
            String room = rs.getString("room");

            String subjectCode = rs.getString("subject_code");
            String subjectName = rs.getString("subject_name");
            String facultyName = rs.getString("faculty_name");

            System.out.println(
                "  " + day +
                " | " + start + " - " + end +
                " | " + subjectCode + " - " + subjectName +
                " | " + facultyName +
                " | " + mode +
                " | " + room
            );
        }

        if (!any) {
            System.out.println("  No schedule found.");
        }

    } catch (SQLException e) {

        System.out.println("Database error while loading schedule.");
        System.out.println(e.getMessage());
    }
  }
        
         /// FACULTY///
   public void viewFaculty() {

    String sql = """
        SELECT
            faculty_id,
            name,
            faculty_rank,
            department,
            email,
            preferred_medium
        FROM faculty
        ORDER BY name
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  #" + rs.getInt("faculty_id")
                + " | " + rs.getString("name")
                + " | " + rs.getString("faculty_rank")
                + " | " + rs.getString("department")
                + " | " + rs.getString("email")
                + " | " + rs.getString("preferred_medium")
            );
        }

        if (!any) {
            System.out.println("  No faculty found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading faculty.");
        System.out.println(e.getMessage());
    }
}
             ///FACULTY MESSAGES ///
    public void sendMessage(int senderId, int receiverId, String message) {

    String sql = """
        INSERT INTO messages (sender_id, receiver_id, message)
        VALUES (?, ?, ?)
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setInt(1, senderId);
        stmt.setInt(2, receiverId);
        stmt.setString(3, message);

        stmt.executeUpdate();

        System.out.println("  Message sent successfully.");

    } catch (SQLException e) {
        System.out.println("Database error while sending message.");
        System.out.println(e.getMessage());
    }
}

          ///FACULTY ID ///
    public int getFacultyUserId(int facultyId) {

    String sql = """
        SELECT user_id
        FROM faculty
        WHERE faculty_id = ?
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setInt(1, facultyId);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("user_id");

            if (!rs.wasNull()) {
                return userId;
            }
        }

    } catch (SQLException e) {
        System.out.println("Database error while finding faculty account.");
        System.out.println(e.getMessage());
    }

    return -1;
}


    // ======================= STUDY TASK MANAGEMENT =======================

    // ADD TASK
    public void addStudyTask(int userId, String task, String dueDate) {
        String sql = """
            INSERT INTO study_tasks (user_id, task, due_date)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, task);

            if (dueDate.isEmpty()) {
                stmt.setNull(3, java.sql.Types.DATE);
            } else {
                stmt.setDate(3, java.sql.Date.valueOf(dueDate));
            }

            stmt.executeUpdate();
            System.out.println("  Task added successfully!");

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("  Error adding task. Check your due date.");
            System.out.println(e.getMessage());
        }
    }
    

    // VIEW TASKS
    public void viewStudyTasks(int userId) {
        String sql = """
            SELECT task_id, task, due_date, status
            FROM study_tasks
            WHERE user_id = ?
            ORDER BY task_id
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n  MY STUDY TASKS");
                System.out.println("  ----------------------------------------------");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("  Task ID: " + rs.getInt("task_id"));
                    System.out.println("  Task: " + rs.getString("task"));

                    java.sql.Date date = rs.getDate("due_date");
                    System.out.println("  Due Date: " +
                        (date == null ? "No due date" : date));

                    System.out.println("  Status: " + rs.getString("status"));
                    System.out.println("  ----------------------------------------------");
                }

                if (!found) {
                    System.out.println("  No study tasks found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("  Error viewing study tasks.");
            System.out.println(e.getMessage());
        }
    }

    // EDIT TASK
    public void updateStudyTask(int taskId, int userId, String task, String dueDate) {
        String sql = """
            UPDATE study_tasks
            SET task = ?, due_date = ?
            WHERE task_id = ? AND user_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task);

            if (dueDate.isEmpty()) {
                stmt.setNull(2, java.sql.Types.DATE);
            } else {
                stmt.setDate(2, java.sql.Date.valueOf(dueDate));
            }

            stmt.setInt(3, taskId);
            stmt.setInt(4, userId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("  Task updated successfully!");
            } else {
                System.out.println("  Task not found.");
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("  Error updating task. Check your due date.");
            System.out.println(e.getMessage());
        }
    }

    // DELETE TASK
    public void deleteStudyTask(int taskId, int userId) {
        String sql = """
            DELETE FROM study_tasks
            WHERE task_id = ? AND user_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("  Task deleted successfully!");
            } else {
                System.out.println("  Task not found.");
            }

        } catch (SQLException e) {
            System.out.println("  Error deleting task.");
            System.out.println(e.getMessage());
        }
    }

    // MARK TASK AS COMPLETE
    public void completeStudyTask(int taskId, int userId) {
        String sql = """
            UPDATE study_tasks
            SET status = 'Completed'
            WHERE task_id = ? AND user_id = ?
              AND status = 'Pending'
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("  Task marked as completed!");
            } else {
                System.out.println("  Pending task not found.");
            }

        } catch (SQLException e) {
            System.out.println("  Error completing task.");
            System.out.println(e.getMessage());
        }
}
    
     /// ANNOUNCEMENTS ///
    public void viewAnnouncements() {

    String sql = """
        SELECT
            announcement_id,
            title,
            content,
            created_at
        FROM announcements
        ORDER BY created_at DESC
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  #" + rs.getInt("announcement_id")
                + " | " + rs.getString("title")
                + " | " + rs.getString("content")
                + " | " + rs.getString("created_at")
            );
        }

        if (!any) {
            System.out.println("  No announcements found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading announcements.");
        System.out.println(e.getMessage());
    }
}
// ======================= REPORTS =======================

public void submitReport(int userId, String subject, String description) {

    String sql = """
        INSERT INTO reports (user_id, subject, description)
        VALUES (?, ?, ?)
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setInt(1, userId);
        stmt.setString(2, subject);
        stmt.setString(3, description);

        stmt.executeUpdate();

        System.out.println("  Report submitted successfully.");

    } catch (SQLException e) {
        System.out.println("Database error while submitting report.");
        System.out.println(e.getMessage());
    }
}

// ======================= REPORTS ======================= //
    public void viewMyReports(int userId) {

    String sql = """
        SELECT
            report_id,
            subject,
            description,
            status,
            created_at
        FROM reports
        WHERE user_id = ?
        ORDER BY created_at DESC
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setInt(1, userId);

        ResultSet rs = stmt.executeQuery();

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  #" + rs.getInt("report_id")
                + " | " + rs.getString("subject")
                + " | " + rs.getString("description")
                + " | Status: " + rs.getString("status")
                + " | " + rs.getString("created_at")
            );
        }

        if (!any) {
            System.out.println("  No reports found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading reports.");
        System.out.println(e.getMessage());
    }
}
    
public void viewPendingReports() {

    String sql = """
        SELECT
            r.report_id,
            u.name AS reporter_name,
            u.role,
            r.subject,
            r.description,
            r.status,
            r.created_at
        FROM reports r
        JOIN users u
            ON r.user_id = u.user_Id
        WHERE r.status = 'Pending'
        ORDER BY r.created_at DESC
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  #" + rs.getInt("report_id")
                + " | " + rs.getString("reporter_name")
                + " | " + rs.getString("role")
                + " | " + rs.getString("subject")
                + " | " + rs.getString("description")
                + " | Status: " + rs.getString("status")
            );
        }

        if (!any) {
            System.out.println("  No pending reports found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading pending reports.");
        System.out.println(e.getMessage());
    }
}


public void viewPendingStudentReports() {

    String sql = """
        SELECT
            r.report_id,
            u.name AS reporter_name,
            r.subject,
            r.description,
            r.status,
            r.created_at
        FROM reports r
        JOIN users u
            ON r.user_id = u.user_Id
        WHERE u.role = 'student'
          AND r.status = 'Pending'
        ORDER BY r.created_at DESC
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  #" + rs.getInt("report_id")
                + " | " + rs.getString("reporter_name")
                + " | " + rs.getString("subject")
                + " | " + rs.getString("description")
                + " | Status: " + rs.getString("status")
            );
        }

        if (!any) {
            System.out.println("  No pending student reports found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading student reports.");
        System.out.println(e.getMessage());
    }
}


    public void forwardReport(int reportId) {

    String sql = """
        UPDATE reports
        SET status = 'Forwarded'
        WHERE report_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, reportId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Report forwarded to admin.");
        } else {
            System.out.println("  Report not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to forward report: " + e.getMessage());
    }
}
    
    
    public void returnReport(int reportId, String note) {

    System.out.println("  Return-for-more-info is not yet stored separately in the database.");
    System.out.println("  Note: " + note);
}

    public void viewFacultySchedule(int userId) {

    String sql = """
        SELECT
            b.block_name,
            sub.subject_code,
            sub.subject_name,
            s.day,
            s.start_time,
            s.end_time,
            s.mode,
            s.room
        FROM schedules s
        JOIN blocks b
            ON s.block_id = b.block_id
        JOIN subjects sub
            ON s.subject_Id = sub.subject_Id
        JOIN faculty f
            ON s.faculty_Id = f.faculty_id
        WHERE f.user_id = ?
        ORDER BY FIELD(
            s.day,
            'Monday',
            'Tuesday',
            'Wednesday',
            'Thursday',
            'Friday',
            'Saturday',
            'Sunday'
        ), s.start_time
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setInt(1, userId);

        ResultSet rs = stmt.executeQuery();

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  " + rs.getString("block_name")
                + " | " + rs.getString("subject_code")
                + " - " + rs.getString("subject_name")
                + " | " + rs.getString("day")
                + " | " + rs.getString("start_time")
                + " - " + rs.getString("end_time")
                + " | " + rs.getString("mode")
                + " | Room: " + rs.getString("room")
            );
        }

        if (!any) {
            System.out.println("  No assigned classes found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading faculty schedule.");
        System.out.println(e.getMessage());
    }
}


    public void viewMyFacultyProfile(int userId) {

    String sql = """
        SELECT
            f.faculty_id,
            f.name,
            f.faculty_rank,
            f.department,
            f.email,
            f.preferred_medium
        FROM faculty f
        WHERE f.user_id = ?
        """;

    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {

        stmt.setInt(1, userId);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {

            System.out.println(
                "  Faculty #: " + rs.getInt("faculty_id")
            );

            System.out.println(
                "  Name: " + rs.getString("name")
            );

            System.out.println(
                "  Rank: " + rs.getString("faculty_rank")
            );

            System.out.println(
                "  Department: " + rs.getString("department")
            );

            System.out.println(
                "  Email: " + rs.getString("email")
            );

            System.out.println(
                "  Preferred Medium: " + rs.getString("preferred_medium")
            );

        } else {

            System.out.println(
                "  No faculty profile is linked to this account yet."
            );
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading faculty profile.");
        System.out.println(e.getMessage());
    }
}


    public void viewAllSchedules() {

    String sql = """
        SELECT
            s.schedule_id,
            s.block_id,
            b.block_name,
            sub.subject_code,
            sub.subject_name,
            f.name AS faculty_name,
            s.day,
            s.start_time,
            s.end_time,
            s.mode,
            s.room
        FROM schedules s
        JOIN blocks b
            ON s.block_id = b.block_id
        JOIN subjects sub
            ON s.subject_Id = sub.subject_Id
        JOIN faculty f
            ON s.faculty_Id = f.faculty_id
        ORDER BY
            b.block_name,
            FIELD(
                s.day,
                'Monday',
                'Tuesday',
                'Wednesday',
                'Thursday',
                'Friday',
                'Saturday'
            ),
            s.start_time
        """;
    
    try (
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()
    ) {

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  #" + rs.getInt("schedule_id")
                + " | " + rs.getString("block_name")
                + " | " + rs.getString("subject_code")
                + " - " + rs.getString("subject_name")
                + " | " + rs.getString("faculty_name")
                + " | " + rs.getString("day")
                + " | " + rs.getString("start_time")
                + " - " + rs.getString("end_time")
                + " | " + rs.getString("mode")
                + " | Room: " + rs.getString("room")
            );
        }

        if (!any) {
            System.out.println("  No schedules found.");
        }

    } catch (SQLException e) {
        System.out.println("Database error while loading schedules.");
        System.out.println(e.getMessage());
    }
}

    public void addSchedule(
        String day,
        int subjectId,
        int facultyId,
        String mode,
        String room,
        String startTime,
        String endTime,
        int blockId) {

    String sql = """
        INSERT INTO schedules
        (day, subject_Id, faculty_Id, mode, room, start_time, end_time, block_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, day);
        ps.setInt(2, subjectId);
        ps.setInt(3, facultyId);
        ps.setString(4, mode);
        ps.setString(5, room);
        ps.setString(6, startTime);
        ps.setString(7, endTime);
        ps.setInt(8, blockId);

        ps.executeUpdate();

        System.out.println("  Schedule added successfully.");

    } catch (SQLException e) {
        System.out.println("  Failed to add schedule: " + e.getMessage());
    }
}

    public void updateSchedule(
        int scheduleId,
        String day,
        int subjectId,
        int facultyId,
        String mode,
        String room,
        String startTime,
        String endTime,
        int blockId) {

    String sql = """
        UPDATE schedules
        SET day = ?,
            subject_Id = ?,
            faculty_Id = ?,
            mode = ?,
            room = ?,
            start_time = ?,
            end_time = ?,
            block_id = ?
        WHERE schedule_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, day);
        ps.setInt(2, subjectId);
        ps.setInt(3, facultyId);
        ps.setString(4, mode);
        ps.setString(5, room);
        ps.setString(6, startTime);
        ps.setString(7, endTime);
        ps.setInt(8, blockId);
        ps.setInt(9, scheduleId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Schedule updated successfully.");
        } else {
            System.out.println("  Schedule not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to update schedule: " + e.getMessage());
    }
}

    public void deleteSchedule(int scheduleId) {

    String sql = """
        DELETE FROM schedules
        WHERE schedule_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, scheduleId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Schedule deleted successfully.");
        } else {
            System.out.println("  Schedule not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to delete schedule: " + e.getMessage());
    }
}

    public void addFaculty(
        String name,
        String facultyRank,
        String department,
        String email,
        String preferredMedium) {

    String sql = """
        INSERT INTO faculty
        (name, faculty_rank, department, email, preferred_medium)
        VALUES (?, ?, ?, ?, ?)
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setString(2, facultyRank);
        ps.setString(3, department);
        ps.setString(4, email);
        ps.setString(5, preferredMedium);

        ps.executeUpdate();

        System.out.println("  Faculty added successfully.");

    } catch (SQLException e) {
        System.out.println("  Failed to add faculty: " + e.getMessage());
    }
}

    public void updateFaculty(
        int facultyId,
        String name,
        String facultyRank,
        String department,
        String email,
        String preferredMedium) {

    String sql = """
        UPDATE faculty
        SET name = ?,
            faculty_rank = ?,
            department = ?,
            email = ?,
            preferred_medium = ?
        WHERE faculty_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setString(2, facultyRank);
        ps.setString(3, department);
        ps.setString(4, email);
        ps.setString(5, preferredMedium);
        ps.setInt(6, facultyId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Faculty updated successfully.");
        } else {
            System.out.println("  Faculty not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to update faculty: " + e.getMessage());
    }
}

    public void deleteFaculty(int facultyId) {

    String sql = """
        DELETE FROM faculty
        WHERE faculty_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, facultyId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Faculty deleted successfully.");
        } else {
            System.out.println("  Faculty not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to delete faculty: " + e.getMessage());
    }
}

    public void viewUsers() {

    String sql = """
        SELECT user_Id, name, email, role
        FROM users
        ORDER BY user_Id
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        boolean found = false;

        while (rs.next()) {
            found = true;

            System.out.println(
                "  [" + rs.getInt("user_Id") + "] "
                + rs.getString("name")
                + " | " + rs.getString("email")
                + " | " + rs.getString("role")
            );
        }

        if (!found) {
            System.out.println("  No users found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to load users: " + e.getMessage());
    }
}

    public void addUser(
        String name,
        String email,
        String password,
        AcadKo.Role role) {

    String sql = """
        INSERT INTO users
        (name, email, password, role)
        VALUES (?, ?, ?, ?)
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.setString(4, roleToDatabaseValue(role));

        ps.executeUpdate();

        System.out.println("  User added successfully.");

    } catch (SQLException e) {
        System.out.println("  Failed to add user: " + e.getMessage());
    }
}

    public void updateUser(
        int userId,
        String name,
        String email,
        String password) {

    String sql = """
        UPDATE users
        SET name = ?,
            email = ?,
            password = ?
        WHERE user_Id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.setInt(4, userId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  User updated successfully.");
        } else {
            System.out.println("  User not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to update user: " + e.getMessage());
    }
}

    public void deleteUser(int userId) {

    String sql = """
        DELETE FROM users
        WHERE user_Id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  User deleted successfully.");
        } else {
            System.out.println("  User not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to delete user: " + e.getMessage());
    }
}

    public void changeUserRole(
        int userId,
        AcadKo.Role role) {

    String sql = """
        UPDATE users
        SET role = ?
        WHERE user_Id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, roleToDatabaseValue(role));
        ps.setInt(2, userId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  User role updated successfully.");
        } else {
            System.out.println("  User not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to change user role: " + e.getMessage());
    }
}

    private String roleToDatabaseValue(AcadKo.Role role) {

    return switch (role) {
        case STUDENT -> "student";
        case REP -> "student representative";
        case PROFESSOR -> "professor";
        case ADMIN -> "admin";
    };
}

    public void addAnnouncement(
        int userId,
        String title,
        String content) {

    String sql = """
        INSERT INTO announcements
        (user_id, title, content)
        VALUES (?, ?, ?)
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.setString(2, title);
        ps.setString(3, content);

        ps.executeUpdate();

        System.out.println("  Announcement posted successfully.");

    } catch (SQLException e) {
        System.out.println("  Failed to post announcement: " + e.getMessage());
    }
}

    public void deleteAnnouncement(int announcementId) {

    String sql = """
        DELETE FROM announcements
        WHERE announcement_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, announcementId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Announcement deleted successfully.");
        } else {
            System.out.println("  Announcement not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to delete announcement: " + e.getMessage());
    }
}
    public void viewReport(int reportId) {

    String sql = """
        SELECT
            r.report_id,
            u.name,
            u.email,
            r.subject,
            r.description,
            r.status,
            r.created_at
        FROM reports r
        JOIN users u ON r.user_id = u.user_Id
        WHERE r.report_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, reportId);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                System.out.println("  Report ID: " + rs.getInt("report_id"));
                System.out.println("  Reporter: " + rs.getString("name"));
                System.out.println("  Email: " + rs.getString("email"));
                System.out.println("  Subject: " + rs.getString("subject"));
                System.out.println("  Description: " + rs.getString("description"));
                System.out.println("  Status: " + rs.getString("status"));
                System.out.println("  Created: " + rs.getTimestamp("created_at"));

            } else {
                System.out.println("  Report not found.");
            }
        }

    } catch (SQLException e) {
        System.out.println("  Failed to view report: " + e.getMessage());
    }
}

    public void resolveReport(int reportId) {

    String sql = """
        UPDATE reports
        SET status = 'Resolved'
        WHERE report_id = ?
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, reportId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("  Report marked as resolved.");
        } else {
            System.out.println("  Report not found.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to resolve report: " + e.getMessage());
    }
}

    public void viewForwardedReports() {

    String sql = """
        SELECT
            r.report_id,
            u.name,
            u.email,
            r.subject,
            r.description,
            r.status,
            r.created_at
        FROM reports r
        JOIN users u ON r.user_id = u.user_Id
        WHERE r.status = 'Forwarded'
        ORDER BY r.report_id
        """;

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        boolean any = false;

        while (rs.next()) {
            any = true;

            System.out.println(
                "  [" + rs.getInt("report_id") + "] "
                + rs.getString("subject")
                + " | " + rs.getString("status")
            );

            System.out.println(
                "      Reporter: " + rs.getString("name")
            );

            System.out.println(
                "      Description: " + rs.getString("description")
            );

            System.out.println();
        }

        if (!any) {
            System.out.println("  No forwarded reports.");
        }

    } catch (SQLException e) {
        System.out.println("  Failed to view forwarded reports: " + e.getMessage());
    }
}

}
