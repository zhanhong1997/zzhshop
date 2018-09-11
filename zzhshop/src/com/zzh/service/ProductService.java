package com.zzh.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.zzh.dao.PageBean;
import com.zzh.dao.ProductDao;
import com.zzh.domain.Category;
import com.zzh.domain.Order;
import com.zzh.domain.OrderItem;
import com.zzh.domain.Product;
import com.zzh.utils.DataSourceUtils;

public class ProductService {

	public List<Product> findHotProduct() {
		ProductDao dao = new ProductDao();
		List<Product> hotProductList = null;
		try {
			hotProductList = dao.findHotProduct();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hotProductList;
	}

	public List<Product> findLatestProduct() {
		ProductDao dao = new ProductDao();
		List<Product> latestProductList = null;
		try {
			latestProductList = dao.findLatestProduct();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return latestProductList;
	}

	public List<Category> findAllCategory() {
		ProductDao dao = new ProductDao();
		List<Category> categoryList = null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return categoryList;
	}

	public PageBean findProductListByCid(String cid,int currentPage,int currentCount) {
		ProductDao dao = new ProductDao();
		PageBean pageBean = new PageBean();

		pageBean.setCurrentPage(currentPage);
		pageBean.setCurrentCount(currentCount);
		
		int totalCount = 0;
		try {
			totalCount = dao.getCount(cid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int totalPage = 0;
		totalPage = (int) Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		
		int index = 0;
		index = (currentPage-1)*currentCount;
		
		List<Product> productList = null;
		try {
			productList= dao.findProuctListByCid(cid,index,currentCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		pageBean.setList(productList);
		return pageBean;
	}

	public Product findProductByPid(String pid) {
		ProductDao dao = new ProductDao();
		Product product = null;
		try {
			product = dao.findProductByPid(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;
	}

	public void submitOrder(Order order) {
		ProductDao dao = new ProductDao();
		
		try {
			DataSourceUtils.startTransaction();
			dao.addOrder(order);
			dao.addOrderItems(order);
		} catch (SQLException e) {
			try {
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateUserInfo(Order order) {
		ProductDao dao = new ProductDao();
		try {
			dao.updateUserInfo(order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateOrderState(String r6_Order) {
		ProductDao dao = new ProductDao();
		try {
			dao.updateOrderState(r6_Order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Order> findAllOrdersByUid(String uid) {
		ProductDao dao = new ProductDao();
		List<Order> orderList = null;
		try {
			orderList = dao.findAllOrdersByUid(uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	public List<Map<String, Object>> findAllOrderItems(String oid) {
		ProductDao dao = new ProductDao();
		List<Map<String, Object>> mapList = null;
		try {
			mapList = dao.findAllOrdersItems(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}
}
