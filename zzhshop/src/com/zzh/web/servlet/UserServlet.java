package com.zzh.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zzh.domain.User;
import com.zzh.service.UserService;

public class UserServlet extends HttpServlet {

	//用户登录
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		//获得输入的用户名和密码
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		//对密码进行加密
		//password = MD5Utils.md5(password);

		//将用户名和密码传递给service层
		UserService service = new UserService();
		User user = null;
		try {
			user = service.login(username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//判断用户是否登录成功 user是否是null
		if(user!=null){
			//登录成功
			//***************判断用户是否勾选了自动登录*****************
			String autoLogin = request.getParameter("autoLogin");
			
			if("autoLogin".equals(autoLogin)){
				//要自动登录
				//创建存储用户名的cookie
				Cookie cookie_username = new Cookie("cookie_username",user.getUsername());
				cookie_username.setMaxAge(60*60);
				//创建存储密码的cookie
				Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
				cookie_password.setMaxAge(60*60);

				response.addCookie(cookie_username);
				response.addCookie(cookie_password);
			}

			//***************************************************
			//将user对象存到session中
			session.setAttribute("user", user);

			//重定向到首页
			response.sendRedirect(request.getContextPath()+"/default.jsp");
		}else{
			request.setAttribute("loginError", "用户名或密码错误");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}