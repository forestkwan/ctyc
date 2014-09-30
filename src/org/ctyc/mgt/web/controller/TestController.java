package org.ctyc.mgt.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ctyc.mgt.test.DineAssignmentTester;

@WebServlet("/test")
public class TestController extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1518398334321489702L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		DineAssignmentTester dineAssignmentTester = new DineAssignmentTester("DineAssignmentTest");
		dineAssignmentTester.testDineAssignment();
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>\n" + "<html>\n" + "<head><title>A Test Servlet</title></head>\n"
				+ "<body bgcolor=\"#fdf5e6\">\n" + "<h1>Test</h1>\n" + "<p>Simple servlet for testing. HAHAHA</p>\n"
				+ "</body></html>");
	}
}