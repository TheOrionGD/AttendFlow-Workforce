package com.ams.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ams.dao.AttendanceDAO;
import com.ams.model.AttendanceRecord;
import com.google.gson.Gson;

public class ViewAttendanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String date = req.getParameter("date");
            List<AttendanceRecord> records = new AttendanceDAO().getAttendanceByDate(date);
            resp.getWriter().write(gson.toJson(records));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(new ErrorResponse("Unable to load attendance records.")));
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
