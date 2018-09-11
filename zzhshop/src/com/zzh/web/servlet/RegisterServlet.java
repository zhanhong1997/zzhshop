package com.zzh.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.zzh.domain.User;
import com.zzh.service.UserService;
import com.zzh.utils.CommonUtils;
import com.zzh.utils.MailUtils;

public class RegisterServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		//获得表单数据
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			ConvertUtils.register(
					new Converter() {
						@Override
						public Object convert(Class clazz, Object value) {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
							Date parse = null;
							try {
								parse = (Date) format.parse(value.toString());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							return parse;
						}
					}
					,Date.class);
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		
		//private String uid;
		user.setUid(CommonUtils.getUUID());
		//private String telephone;
		user.setTelephone(null);
		//private int state;
		user.setState(0);
		//private String code;
		String activeCode = CommonUtils.getUUID();
		user.setCode(activeCode);
		
		boolean isRegisterSuccess = false;
		UserService service = new UserService();
		try {
			isRegisterSuccess = service.register(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(isRegisterSuccess) {
			String emailMsg = "恭喜您成功注册本网站账号，请点击下列链接进行注册激活"+
								"<a href='http://localhost:8080/zzhshop/active?activeCode="+activeCode+"'>"+
								"http://localhost:8080/zzhshop/active?activeCode="+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
								
			response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
		}else {
			response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}