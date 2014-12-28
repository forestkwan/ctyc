package org.ctyc.mgt.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ctyc.mgt.test.DineAssignmentTester;
import org.ctyc.mgt.test.ReadAndWriteFileTester;

@WebServlet("/testWriteFile")
public class TestWriteFileController extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1518398334321489702L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ReadAndWriteFileTester tester = new ReadAndWriteFileTester();
		tester.testWriteParticipants();
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>\n" + "<html>\n" + "<head><title>A Test Servlet</title></head>\n"
				+ "<body bgcolor=\"#fdf5e6\">\n" + "<h1>Test Write File</h1>\n"
				+ "</body></html>");
	}
}