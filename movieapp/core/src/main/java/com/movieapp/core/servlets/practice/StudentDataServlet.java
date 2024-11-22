package com.movieapp.core.servlets.practice;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;
import java.io.IOException;
import java.sql.*;

@Component(service = Servlet.class, property = { "sling.servlet.paths=" + "/bin/studentdata" })
public class StudentDataServlet extends SlingAllMethodsServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/student";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Dundu@003";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String htmlResponse = "<html><body><h2>Student Data</h2>";

        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT studentid, name, email FROM students";
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                htmlResponse += "<p>No student data available.</p>";
            }

            while (rs.next()) {
                int studentId = rs.getInt("studentid");
                String name = rs.getString("name");
                String email = rs.getString("email");

                htmlResponse += "<tr>" +
                        "<td>" + studentId + "</td>" +
                        "<td>" + name + "</td>" +
                        "<td>" + email + "</td>" +
                        "<td>" +
                        "<a href='/bin/studentdata?studentid=" + studentId + "&action=edit'>Edit</a> | " +
                        "<a href='/bin/studentdata?studentid=" + studentId + "&action=delete'>Delete</a>" +
                        "</td>" +
                        "</tr>";
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            htmlResponse += "<p>Error connecting to the database.</p>";
        }

        htmlResponse += "</tbody></table>";
        response.setContentType("text/html");
        response.getWriter().write(htmlResponse);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String studentIdStr = request.getParameter("studentid");
        String updateName = request.getParameter("update_name");
        String updateEmail = request.getParameter("update_email");
        String action = request.getParameter("action");

        Connection conn = null;
        PreparedStatement ps = null;
        Statement stmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            if (action == null) {
                String insertSQL = "INSERT INTO students (name, email) VALUES (?, ?)";
                ps = conn.prepareStatement(insertSQL);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.executeUpdate();
                response.getWriter().write("Student added successfully!");
            } else if ("Update".equals(action)) {
                if (studentIdStr != null && !studentIdStr.isEmpty()) {
                    int studentId = Integer.parseInt(studentIdStr);
                    String updateSQL = "UPDATE students SET name = ?, email = ? WHERE studentid = ?";
                    ps = conn.prepareStatement(updateSQL);
                    ps.setString(1, updateName != null ? updateName : name);
                    ps.setString(2, updateEmail != null ? updateEmail : email);
                    ps.setInt(3, studentId);
                    ps.executeUpdate();
                    response.getWriter().write("Student updated successfully!");
                } else {
                    response.getWriter().write("Student ID is required for updating!");
                }
            } else if ("Delete".equals(action)) {
                if (studentIdStr != null && !studentIdStr.isEmpty()) {
                    int studentId = Integer.parseInt(studentIdStr);

                    String checkSQL = "SELECT COUNT(*) FROM students WHERE studentid = ?";
                    ps = conn.prepareStatement(checkSQL);
                    ps.setInt(1, studentId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        int count = rs.getInt(1);
                        if (count > 0) {
                            String deleteSQL = "DELETE FROM students WHERE studentid = ?";
                            ps = conn.prepareStatement(deleteSQL);
                            ps.setInt(1, studentId);
                            ps.executeUpdate();
                            response.getWriter().write("Student deleted successfully!");
                        } else {
                            response.getWriter().write("Error: The student with ID " + studentId + " does not exist and cannot be deleted.");
                        }
                    }
                    rs.close();
                } else {
                    response.getWriter().write("Student ID is required for deletion!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("An error occurred while processing your request.");
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
