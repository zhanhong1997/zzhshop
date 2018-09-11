package com.zzh.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.zzh.domain.Category;
import com.zzh.domain.Order;
import com.zzh.service.AdminService;

public class AdminServlet extends BaseServlet {
	
	public void findOrderInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InterruptedException {
		Thread.sleep(3000);
		String oid = request.getParameter("oid");
		AdminService service = new AdminService();
		List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);
		
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	
	
	
	public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdminService service = new AdminService();
		List<Order> orderList = service.findAllOrders();
		
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
	}
	
	
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdminService service = new AdminService();
		List<Category> categoryList = service.findAllCategory();
		
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
}