package com.zzh.service;

import java.sql.SQLException;

import com.zzh.dao.UserDao;
import com.zzh.domain.User;

public class UserService {

	public boolean register(User user) throws SQLException {
		UserDao dao = new UserDao();
		int row = dao.register(user);
		return row>0?true:false;
	}

	public void active(String activeCode) throws SQLException {
		UserDao dao = new UserDao();
		dao.active(activeCode);
	}

	public boolean isExist(String username) {
		UserDao dao = new UserDao();
		Long isExist = 0L;
		try {
			isExist = dao.isExist(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isExist>0?true:false;
	}

	public User login(String username, String password) throws SQLException {
		UserDao dao = new UserDao();
		User user = dao.login(username,password);
		return user;
	}

}
