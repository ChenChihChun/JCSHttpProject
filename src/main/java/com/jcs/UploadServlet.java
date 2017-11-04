package com.jcs;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

//import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
//import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.io.output.*;

/***
 * easy http file upload servlet
 * @author Peter
 *
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 10 * 1024 * 1024; //10M
	private int maxMemSize = 128 * 1024; //128K
	private File file;

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getInitParameter("file-upload");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		// java.io.PrintWriter out = response.getWriter( );

		if (!isMultipart) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// maximum size that will be stored in memory
			factory.setSizeThreshold(maxMemSize);

			// Location to save data that is larger than maxMemSize.
			factory.setRepository(new File("c:\\temp"));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// maximum file size to be uploaded.
			upload.setSizeMax(maxFileSize);
			
			// Parse the request to get file items.
			List<?> fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator<?> i = fileItems.iterator();

			boolean isPassed = true;
			String uploadPath = "";
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (fi.isFormField()) {
					String fieldName = fi.getFieldName();
					String value = fi.getString();
					// simple certificate
					if ("ac".equals(fieldName)) {
						if ("jcs".equals(URLDecoder.decode(value, "UTF-8"))) {
							isPassed = isPassed && true;
						} else {
							isPassed = isPassed && false;
						}
					}
					if ("pd".equals(fieldName)) {
						if ("jcsRU*T?^".equals(URLDecoder.decode(value, "UTF-8"))) {
							isPassed = isPassed && true;
						} else {
							isPassed = isPassed && false;
						}
					}
					if ("uploadPath".equals(fieldName)) {
						uploadPath = URLDecoder.decode(value, "UTF-8");
					}
				} else if (!fi.isFormField() && isPassed) {
					String fileName = fi.getName();
					// Write the file
					File folder = new File(filePath,uploadPath);
					if (!folder.exists()) {
						folder.mkdirs();
					}
					file = new File(folder, fileName);
					fi.write(file);
				}
			}
			if (!isPassed) {
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (SizeLimitExceededException ex) { 
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}
