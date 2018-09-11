package com.zzh.web.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.zzh.domain.Category;
import com.zzh.domain.Product;
import com.zzh.service.AdminService;
import com.zzh.utils.CommonUtils;

public class AdminAddProductServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Product product = new Product();
		Map<String,Object> map = new HashMap<String,Object>();
		
		//上传图片到服务器的功能 并获取普通文本项
		try {
			//创建磁盘文件项工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//创建文件上传的核心类
			ServletFileUpload upload = new ServletFileUpload(factory);
			//机械request 获得文件项集合
			List<FileItem> parseRequest = upload.parseRequest(request);
			//遍历文件项集合
			for(FileItem item :parseRequest) {
				boolean formfield = item.isFormField();
				if(formfield) {
					String fieldName = item.getFieldName();
					String fieldValue = item.getString("UTF-8");
					map.put(fieldName, fieldValue);
				}else {
					String fileName = item.getName();
					InputStream in = item.getInputStream();
					String path = this.getServletContext().getRealPath("upload");
					OutputStream out = new FileOutputStream(path+"/"+fileName);
					int len = 0;
					byte[] buff = new byte[1024];
					while((len=in.read(buff))>0) {
						out.write(buff, 0, len);
					}
					in.close();
					out.close();
					item.delete();
					map.put("pimage", "upload/"+fileName);
				}
			}
			
			BeanUtils.populate(product, map);
			//private String pid;
			product.setPid(CommonUtils.getUUID());
			//private Date pdate;
			product.setPdate(new Date());
			//private int pflag;
			product.setPflag(0);
			//private Category category;
			Category category = new Category();
			category.setCid(map.get("cid").toString());
			product.setCategory(category);
			
			AdminService service = new AdminService();
			service.saveProduct(product);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}