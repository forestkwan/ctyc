package org.ctyc.mgt.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.summercamp.SummerCampService;

@WebServlet("/exportAssignment")
public class ExportAssignmentServlet extends HttpServlet {
	
	private static final String EXPORT_FILE_PATH = "C:\\gitvob\\ctyc\\WebContent\\SummerCamp\\download\\assignment.sav";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		response.setHeader("Cache-Control", "nocache");
		response.setCharacterEncoding("utf-8");
		
		System.out.println("Export All Assignment");
		
		PrintWriter out = response.getWriter();
		out.print(this.getDownloadUrl());
	}
	
	private String getDownloadUrl(){
		
		File from = new File(SummerCampService.DINE_ASSIGNMENT_PLAN_PATH);
		File copied = new File(EXPORT_FILE_PATH);
		
		try {
			com.google.common.io.Files.copy(from, copied);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "/CTYCManagement/SummerCamp/download/assignment.sav";
	}
	
}
