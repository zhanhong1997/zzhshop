package com.zzh.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.service.UserService;

public class ActiveServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String activeCode = request.getParameter("activeCode");
		UserService service = new UserService();
		try {
			service.active(activeCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}