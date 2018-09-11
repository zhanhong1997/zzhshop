package com.zzh.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.zzh.domain.Category;
import com.zzh.domain.Order;
import com.zzh.domain.Product;
import com.zzh.utils.DataSourceUtils;

public class AdminDao {

	public List<Category> findAllCategory() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from category";
		return runner.query(sql, new BeanListHandler<Category>(Category.class));
	}

	public void saveProduct(Product product) throws SQLException {
		QueryRunner runner = new QueryRunner();
		Connection conn = DataSourceUtils.getConnection();
		String sql = "insert into product values(?,?,?,?,?,?,?,?,?,?)";
		runner.update(conn, sql, product.getPid(),product.getPname(),product.getMarket_price(),product.getShop_price(),
				product.getPimage(),product.getPdate(),product.getIs_hot(),product.getPdesc(),
				product.getPflag(),product.getCategory().getCid());
	}

	public List<Order> findAllOrders() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from orders";
		return runner.query(sql, new BeanListHandler<Order>(Order.class));
	}

	public List<Map<String,Object>> findOrderInfoByOid(String oid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select p.pimage,p.pname,p.shop_price,i.count,i.subtotal from product p,orderitem i"+
						" where p.pid=i.pid and i.oid=?";
		/*String sql = "select p.pimage,p.pname,p.shop_price,i.count,i.subtotal "+
				" from orderitem i,product p "+
				" where i.pid=p.pid and i.oid=? ";*/
		return runner.query(sql,new MapListHandler(),oid);
	}
}
