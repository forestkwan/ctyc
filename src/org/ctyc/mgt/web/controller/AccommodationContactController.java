package org.ctyc.mgt.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ctyc.mgt.model.summercamp.AccommodationContact;
import org.ctyc.mgt.summercamp.accommodation.AccommodationService;

import com.google.gson.Gson;

@WebServlet("/accommodationcontact")
public class AccommodationContactController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7438139862324010009L;
	
	private AccommodationService accommodationService = new AccommodationService();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String camp = request.getParameter("camp");
		AccommodationContact accommodationContact = accommodationService.getAccommodationContact(camp);
		
		String json = new Gson().toJson(accommodationContact);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		AccommodationContact accommodationContact = new AccommodationContact();
		
		String camp = request.getParameter("camp");
		
		String json = new Gson().toJson(accommodationContact);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}
}
