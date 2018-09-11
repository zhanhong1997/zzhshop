package com.zzh.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;
import com.zzh.dao.PageBean;
import com.zzh.domain.Cart;
import com.zzh.domain.CartItem;
import com.zzh.domain.Category;
import com.zzh.domain.Order;
import com.zzh.domain.OrderItem;
import com.zzh.domain.Product;
import com.zzh.domain.User;
import com.zzh.service.ProductService;
import com.zzh.utils.CommonUtils;
import com.zzh.utils.PaymentUtil;

public class ProductServlet extends BaseServlet {

/*	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String methodName = request.getParameter("method");
		if("categoryList".equals(methodName)) {
			categoryList(request,response);
		}else if("index".equals(methodName)){
			index(request,response);
		}else if("productInfo".equals(methodName)){
			productInfo(request,response);
		}else if("productListByCid".equals(methodName)){
			productListByCid(request,response);
		}
	}*/

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user= (User) request.getAttribute("user");
		ProductService service = new ProductService();
		List<Order> orderList = service.findAllOrdersByUid(user.getUid());
		
		if(orderList!=null) {
			for(Order order :orderList) {
				String oid = order.getOid();
				List<Map<String, Object>> mapList = service.findAllOrderItems(oid);
				for(Map<String,Object> map :mapList) {
					try {
						OrderItem orderItem = new OrderItem();
						BeanUtils.populate(orderItem, map);
						Product product = new Product();
						BeanUtils.populate(product, map);
						orderItem.setProduct(product);
						order.getOrderItems().add(orderItem);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
		
	}
	
	
	//更新收货人信息
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> properties = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		ProductService service = new ProductService();
		service.updateUserInfo(order);
		
		/*if(pd_FrpId.equals("ABC-NET-B2C")) {
			
		}else if(pd_FrpId.equals("ICBC-NET-B2C")) {
			
		}
		*/
		String orderid = request.getParameter("oid");
		String money = request.getParameter("money");
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
//		String p3_Amt = money;
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		
		
		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
						"&p0_Cmd="+p0_Cmd+
						"&p1_MerId="+p1_MerId+
						"&p2_Order="+p2_Order+
						"&p3_Amt="+p3_Amt+
						"&p4_Cur="+p4_Cur+
						"&p5_Pid="+p5_Pid+
						"&p6_Pcat="+p6_Pcat+
						"&p7_Pdesc="+p7_Pdesc+
						"&p8_Url="+p8_Url+
						"&p9_SAF="+p9_SAF+
						"&pa_MP="+pa_MP+
						"&pr_NeedResponse="+pr_NeedResponse+
						"&hmac="+hmac;

		//重定向到第三方支付平台
		response.sendRedirect(url);
	}
	
	
	//提交订单
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Order order = new Order();
		//private String oid;
		order.setOid(CommonUtils.getUUID());
		//private Date ordertime;
		order.setOrdertime(new Date());
		//private double total;
		Cart cart = (Cart) session.getAttribute("cart");
		order.setTotal(cart.getTotal());
		//private int state;
		order.setState(0);
		//private String address;
		order.setAddress(null);
		//private String name;
		order.setName(null);
		//private String telephone;
		order.setTelephone(null);
		//private User user;
		order.setUser(user);
		
		//private List<OrderItem> orderItems = new ArrayList<OrderItem>();
		Map<String, CartItem> cartItems = cart.getCartItems();
		for(Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
			CartItem cartItem = entry.getValue();
			OrderItem orderItem = new OrderItem();
			//private String itemid;
			orderItem.setItemid(CommonUtils.getUUID());
			//private int count;
			orderItem.setCount(cartItem.getBuyNum());
			//private double subtotal;
			orderItem.setSubtotal(cartItem.getSubtotal());
			//private Product product;
			orderItem.setProduct(cartItem.getProduct());
			//private Order order;
			orderItem.setOrder(order);
			order.getOrderItems().add(orderItem);
		}
		
		ProductService service = new ProductService();
		service.submitOrder(order);
		
		session.setAttribute("order", order);
		response.sendRedirect(request.getContextPath()+"/order_info.jsp");
	}
	
	
	//清空购物车
	public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	
	//将购物车中的商品删掉
	public void delProductFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		HttpSession session = request.getSession();
		
		Cart cart = (Cart) session.getAttribute("cart");
		double subtotal = 0.0;
		if(cart!=null) {
			Map<String, CartItem> cartItems = cart.getCartItems();
			CartItem cartItem = cartItems.get(pid);
			subtotal = cartItem.getSubtotal();
			cartItems.remove(pid);
			cart.setCartItems(cartItems);
		}
		
		cart.setTotal(cart.getTotal()-subtotal);
		session.setAttribute("cart", cart);
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//将商品添加到购物车
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String pid = request.getParameter("pid");
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));
		
		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);
		
		CartItem cartItem = new CartItem();
		//计算小计
		double subtotal = product.getShop_price()*buyNum;
		
		cartItem.setProduct(product);
		cartItem.setBuyNum(buyNum);
		cartItem.setSubtotal(subtotal);
		
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart==null) {
			cart = new Cart();
		}
		
		Map<String, CartItem> cartItems = cart.getCartItems();
		double oldsubtotal = 0.0;
		double newsubtotal = 0.0;
		int newbuyNum = 0;
		if(cartItems.containsKey(pid)) {
			CartItem oldcartItem = cartItems.get(pid);
			
			newbuyNum = buyNum+oldcartItem.getBuyNum();
			
			newsubtotal = subtotal;
			cartItem.setSubtotal(newsubtotal+oldcartItem.getSubtotal());
			
			cartItem.setBuyNum(newbuyNum);
			cartItems.put(pid, cartItem);
		}else {
			cartItems.put(pid,cartItem);
			newsubtotal = subtotal;
		}
		
		double total = cart.getTotal()+newsubtotal;
		cart.setTotal(total);
		
		
		
		session.setAttribute("cart", cart);;
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	
	//获得商品分类的功能
	public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		List<Category> categoryList = service.findAllCategory();
		
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	
	
	//获得商城首页的功能
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();
		//准备热门商品
		List<Product> hotProductList = service.findHotProduct();
		//准备最新商品
		List<Product> latestProductList = service.findLatestProduct();
		
		
		request.setAttribute("hotProductList",hotProductList);
		request.setAttribute("latestProductList",latestProductList);
		//request.setAttribute("categoryList", categoryList);
		
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	
	
	//获得商品具体信息的功能
	public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		String cid = request.getParameter("cid");
		String currentPageStr = request.getParameter("currentPage");
		int currentPage = Integer.parseInt(currentPageStr);
		
		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);
		
		request.setAttribute("product", product);
		request.setAttribute("cid", cid);
		request.setAttribute("currentPage", currentPage);

		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie :cookies) {
				if("pids".equals(cookie.getName())){
					pids = cookie.getValue();
					String[] split = pids.split("-");
					List<String> asList = Arrays.asList(split);
					LinkedList list = new LinkedList<String>(asList);
					
					if(list.contains(pid)) {
						list.remove(pid);
						list.addFirst(pid);
					}else {
						list.addFirst(pid);
					}
					
					StringBuffer sb = new StringBuffer();
					for(int i=0;i<list.size()&&i<7;i++) {
						sb.append(list.get(i));
						sb.append("-");
					}
					pids = sb.substring(0, sb.length()-1);
				}
			}
		}
		
		Cookie cookie_history = new Cookie("pids", pids);
		response.addCookie(cookie_history);
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
	
	
	//通过cid获得商品列表的功能
	public void productListByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		String currentPageStr = request.getParameter("currentPage");
		if(currentPageStr==null) {
			currentPageStr = "1";
		}
		int currentPage = Integer.parseInt(currentPageStr);
		int currentCount = 12; 
		
		ProductService service = new ProductService();
		PageBean pageBean = service.findProductListByCid(cid,currentPage,currentCount);
		
		List<Product> historyProductList = new ArrayList<Product>();
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie :cookies) {
				if("pids".equals(cookie.getName())) {
					String pids = cookie.getValue();
					String[] split = pids.split("-");
					for(int i=0;i<split.length;i++) {
						Product product = service.findProductByPid(split[i]);
						historyProductList.add(product);
					}
				}
			}
		}

		request.setAttribute("historyProductList", historyProductList);
		request.setAttribute("cid", cid);
		request.setAttribute("pageBean", pageBean);
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);;
	}

}