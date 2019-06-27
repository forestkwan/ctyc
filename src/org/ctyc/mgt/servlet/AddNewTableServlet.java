package org.ctyc.mgt.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ctyc.mgt.model.summercamp.CampName;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.summercamp.SummerCampService;

@WebServlet("/addNewTable")
public class AddNewTableServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("Add New Table");
		
		String campSiteCode = request.getParameter("camp");
		CampName campLocation = CampName.valueOf(request.getParameter("campLocation"));
		DineTimeSlot.TimeOfDay timeOfDine = DineTimeSlot.TimeOfDay.valueOf(request.getParameter("timeOfDine"));
		Integer day = Integer.parseInt(request.getParameter("day"));
		
		SummerCampService.getInstance().addNewTable(campSiteCode, campLocation, timeOfDine, day);
	}
	
}
