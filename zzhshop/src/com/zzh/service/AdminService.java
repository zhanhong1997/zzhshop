package com.zzh.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.zzh.dao.AdminDao;
import com.zzh.domain.Category;
import com.zzh.domain.Order;
import com.zzh.domain.Product;

public class AdminService {

	public List<Category> findAllCategory() {
		AdminDao dao = new AdminDao();
		List<Category> categoryList = null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	public void saveProduct(Product product) {
		AdminDao dao = new AdminDao();
		try {
			dao.saveProduct(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Order> findAllOrders() {
		AdminDao dao = new AdminDao();
		List<Order> orderList = null;
		try {
			orderList = dao.findAllOrders();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	public List<Map<String,Object>> findOrderInfoByOid(String oid) {
		AdminDao dao = new AdminDao();
		List<Map<String,Object>> mapList = null;
		try {
			mapList = dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}
}
