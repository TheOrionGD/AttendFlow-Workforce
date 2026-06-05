package com.ams.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ams.dao.StudentDAO;
import com.ams.model.Student;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<Student> students = new StudentDAO().getAllStudents();
            resp.getWriter().write(gson.toJson(students));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Unable to load students.")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (BufferedReader reader = req.getReader()) {
            StudentRequest request = gson.fromJson(reader, StudentRequest.class);
            if (request == null || request.name == null || request.name.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new ErrorResponse("Invalid student name.")));
                return;
            }
            new StudentDAO().addStudent(request.name.trim());
            resp.getWriter().write(gson.toJson(new SimpleResponse("Student added successfully.")));
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Malformed JSON payload.")));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Unable to add student.")));
        }
    }

    private static class StudentRequest {
        String name;
    }

    private static class SimpleResponse {
        private final String message;

        public SimpleResponse(String message) {
            this.message = message;
        }
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
