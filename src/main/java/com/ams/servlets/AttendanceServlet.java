package com.ams.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ams.dao.AttendanceDAO;
import com.ams.model.AttendanceRecord;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AttendanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (BufferedReader reader = req.getReader()) {
            AttendanceRequest request = gson.fromJson(reader, AttendanceRequest.class);
            if (request == null || request.date == null || request.date.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(new SimpleResponse("Invalid attendance payload.")));
                return;
            }

            List<AttendanceRecord> records = new ArrayList<>();
            if (request.records != null) {
                for (Record item : request.records) {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setStudentId(item.studentId);
                    record.setStatus(item.status != null ? item.status : "Absent");
                    records.add(record);
                }
            }

            new AttendanceDAO().saveAttendance(request.date, records);
            resp.getWriter().write(gson.toJson(new SimpleResponse("Attendance submitted successfully.")));
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(new SimpleResponse("Malformed JSON payload.")));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new SimpleResponse("Unable to save attendance.")));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(new SimpleResponse("Attendance endpoint. Use POST to submit attendance.")));
    }

    @SuppressWarnings("unused")
    private static class AttendanceRequest {
        String date;
        List<Record> records;
    }

    @SuppressWarnings("unused")
    private static class Record {
        int studentId;
        String status;
    }

    @SuppressWarnings("unused")
    private static class SimpleResponse {
        private final String message;

        public SimpleResponse(String message) {
            this.message = message;
        }
    }
}
