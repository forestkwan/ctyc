package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.CanteenTable;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.ctyc.mgt.summercamp.costfunction.SameSundayClassCostFunction;
import org.ctyc.mgt.utils.CsvReader;
import org.ctyc.mgt.utils.FileUtils;
import org.ctyc.mgt.websocket.Message;

public class SummerCampService {
	
	private static String SERVER_RESPONSE = "SERVER_RESPONSE";
	private static String GET_CAMP_SITE = "GET_CAMP_SITE";
	private static String CAMP_SITE_DATA = "CAMP_SITE_DATA";
	private static String UPDATE_DINE_TABLE = "UPDATE_DINE_TABLE";
	private static String GET_DINE_ASSIGNMENT = "GET_DINE_ASSIGNMENT";
	private static String DINE_ASSIGNMENT_DATA = "DINE_ASSIGNMENT_DATA";
	private static String UPDATE_DINE_ASSIGNMENT = "UPDATE_DINE_ASSIGNMENT";
	
	private static String CAMP_SITE_PATH = "c:\\CTYCSave\\CampSite.txt";
	private static String DINE_ASSIGNMENT_PLAN_PATH = "c:\\CTYCSave\\DineAssignmentPlan.txt";
	
	private static String[] campNames = {"A", "B"};
	
	private static SummerCampService instance = null;
	private Map<String, CampSite> campSiteMap = null;
	private Map<String, DineAssignmentPlan> dineAssignmentPlanMap = null;

	protected SummerCampService(){
		// Load saved camp site
		this.initCampSiteMap();
		this.initDineAssignmentPlanMap();
	}
	
	private void initCampSiteMap(){
		
		this.campSiteMap = FileUtils.readFileToObject(CAMP_SITE_PATH);
		
		if (this.campSiteMap == null){
			this.campSiteMap = new HashMap<String, CampSite>();
			
			for (String campName : campNames){
				CampSite campSite = new CampSite();
				campSite.setName(campName);
				campSite.getParticipants().addAll(CsvReader.readParticipantCsv("c:\\CTYCSave\\camp" + campName + "_panticipants.csv"));
				
				this.campSiteMap.put(campName, campSite);
			}
			
			this.saveCampSiteToFile();
		}
	}
	
	private void initDineAssignmentPlanMap(){
		
		this.dineAssignmentPlanMap = FileUtils.readFileToObject(DINE_ASSIGNMENT_PLAN_PATH);
		
		if (this.dineAssignmentPlanMap == null){
			
			this.dineAssignmentPlanMap = new HashMap<String, DineAssignmentPlan>();
			Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
//			costFunctions.add(new GenderBalanceCostFunction(1, 1));
//			costFunctions.add(new SameGroupCostFunction(1, 1));
			costFunctions.add(new SameSundayClassCostFunction(1, 1));
			
			Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
//			constraintFunctions.add(new MentorInTableCostFunction(1, 1));
//			constraintFunctions.add(new FamilyGroupCostFunction(1, 1));
			
			for (String campName : campNames){
				
				CampSite campSite = this.campSiteMap.get(campName);
				if (campSite == null){
					continue;
				}
				
				DineAssignmentManager dineAssignmentManager = new DineAssignmentManager(campSite.getParticipants(), 8, costFunctions, constraintFunctions, 1);
				dineAssignmentManager.doAssignment();
				DineAssignmentPlan dineAssignmentPlan = dineAssignmentManager.getAssignmentPlan();
				
				if (dineAssignmentPlan != null){
					this.dineAssignmentPlanMap.put(campName, dineAssignmentPlan);
				}
			}
		}
		
		this.saveDineTableAssignmentToFile();
	}
	
	protected void saveCampSiteToFile(){
		FileUtils.writeObjectToFile(this.campSiteMap, CAMP_SITE_PATH);
	}
	
	protected void saveDineTableAssignmentToFile(){
		FileUtils.writeObjectToFile(this.dineAssignmentPlanMap, DINE_ASSIGNMENT_PLAN_PATH);
	}
	
	public static SummerCampService getInstance() {
		if(instance == null) {
			instance = new SummerCampService();
		}
		return instance;
	}
	
	/*
	 * This method process the client web socket message 
	 * Return null if no need to send call back message */
	public Message processClientMessage(Message requestMessage){
		
		Message responseMessage = null;
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), GET_CAMP_SITE)){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("campSites", this.campSiteMap);
			responseMessage = new Message(CAMP_SITE_DATA, data);
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), GET_DINE_ASSIGNMENT)){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("dineAssignment", this.dineAssignmentPlanMap);
			responseMessage = new Message(DINE_ASSIGNMENT_DATA, data);
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), UPDATE_DINE_TABLE)){
			responseMessage = this.updateDineTable(requestMessage.getData());
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), UPDATE_DINE_ASSIGNMENT)){
			responseMessage = this.updateDineAssignment(requestMessage.getData());
		}
		
		return responseMessage;
	}

	private Message updateDineTable(Map<String, Object> data) {
		
		if (data == null || data.get("dineTables") == null || data.get("camp") == null){
			return null;
		}
		
		String campName = data.get("camp").toString();
		CampSite campSite = this.campSiteMap.get(campName);
		
		if (campSite == null){
			return null;
		}
		campSite.getCanteenTables().clear();
		
		List<Map<String, Object>> dineTableMaps = (List<Map<String, Object>>) data.get("dineTables");
		for (Map<String, Object> dineTableMap : dineTableMaps){
			
			int tableNumber = Integer.valueOf(dineTableMap.get("number").toString());
			int capacity = Integer.valueOf(dineTableMap.get("capacity").toString());
			
			CanteenTable canteenTable = new CanteenTable(tableNumber, capacity);
			campSite.getCanteenTables().add(canteenTable);
		}
		
		this.saveCampSiteToFile();
		
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("isSuccess", true);
		return new Message(SERVER_RESPONSE, responseData);
	}
	
	private Message updateDineAssignment(Map<String, Object> data) {
		
		if (data == null || data.get("camp") == null || data.get("dineTableGroups") == null){
			return null;
		}
		
		String campName = data.get("camp").toString();
		DineAssignmentPlan dineAssignmentPlan = this.dineAssignmentPlanMap.get(campName);
		
		if (dineAssignmentPlan == null){
			return null;
		}
		//dineAssignmentPlan.getDineTableGroups().clear();
		
		List<Map<String, Object>> dineTableGroupsDataList = (List<Map<String, Object>>) data.get("dineTableGroups");
		
		for (Map<String, Object> dineTableGroupsData : dineTableGroupsDataList){
			Integer tableNumber = Integer.valueOf(dineTableGroupsData.get("tableNumber").toString());
			List<Map<String, Object>> participantDataList = (List<Map<String, Object>>) dineTableGroupsData.get("participants");
		}
		
		/*
		this.saveCampSiteToFile();*/
		
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("isSuccess", true);
		return new Message(SERVER_RESPONSE, responseData);
	}
	
}
