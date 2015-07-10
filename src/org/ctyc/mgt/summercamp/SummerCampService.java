package org.ctyc.mgt.summercamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.CanteenTable;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.DineTimeStatistics;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.ctyc.mgt.summercamp.costfunction.FamilyGroupCostFunction;
import org.ctyc.mgt.summercamp.costfunction.GenderBalanceCostFunction;
import org.ctyc.mgt.summercamp.costfunction.MentorInTableCostFunction;
import org.ctyc.mgt.summercamp.costfunction.SameGroupCostFunction;
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
	private static String GROUP_ASSIGNMENT_DATA = "GROUP_ASSIGNMENT_DATA";
	private static String UPDATE_DINE_ASSIGNMENT = "UPDATE_DINE_ASSIGNMENT";
	private static String UPDATE_DINE_ASSIGNMENT_COMPLETE = "UPDATE_DINE_ASSIGNMENT_COMPLETE";
	private static String AUTO_ASSIGN = "AUTO_ASSIGN";
	private static String AUTO_ASSIGN_COMPLETE = "AUTO_ASSIGN_COMPLETE";
	private static String CALCULATE_COST = "CALCULATE_COST";
	private static String CALCULATE_COST_COMPLETE = "CALCULATE_COST_COMPLETE";
	
	private static String CAMP_SITE_PATH = "CTYCSave/CampSite.txt";
	private static String DINE_ASSIGNMENT_PLAN_PATH = "CTYCSave/DineAssignmentPlan.txt";
	private static String SAVE_HOME;
	
	private static String[] campNames = {"A", "B"};
	
	private static SummerCampService instance = null;
	private Map<String, CampSite> campSiteMap = null;
	private Collection<DineAssignmentPlan> dineAssignmentPlanList = null;
	private Map<String, Participant> participantMap = null;
	
	static {
		SAVE_HOME = System.getenv("SAVE_HOME");
		
		if (SystemUtils.IS_OS_WINDOWS){
			
			if (SAVE_HOME == null){
				SAVE_HOME = "c:\\CTYCSave";
			}
			
			CAMP_SITE_PATH = SAVE_HOME + "\\CampSite.txt";
			DINE_ASSIGNMENT_PLAN_PATH = SAVE_HOME + "\\DineAssignmentPlan.txt";
			
		} else {
			
			if (SAVE_HOME == null){
				SAVE_HOME = "CTYCSave";
			}
			
			CAMP_SITE_PATH = SAVE_HOME + "/CampSite.txt";
			DINE_ASSIGNMENT_PLAN_PATH = SAVE_HOME + "/DineAssignmentPlan.txt";
		}
	}

	protected SummerCampService(){
		// Load saved camp site
		init();
	}
	
	private void init() {
		this.initCampSiteMap();
		this.initDineAssignmentPlanMap();
		this.initParticipantMap();
	}
	
	private void initCampSiteMap(){
		// force refreshing camp site data
//		String resourcePath = "/CampSite.txt";
//		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
//		this.campSiteMap = FileUtils.readInputStreamToObject(inputStream);
		
		// force refreshing camp site data
		this.campSiteMap = null;
		
		if (this.campSiteMap == null){
			this.campSiteMap = new HashMap<String, CampSite>();
			
			Authenticator.setDefault(new Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication ("admin", "ctycAug08".toCharArray());
			    }
			});
			
			for (String campName : campNames){
				CampSite campSite = new CampSite();
				campSite.setName(campName);
				
				try {
					URL url = null;
					if (campName.equals("A")) {
						url = new URL("http://www.ctyc.org.hk/summer/enrollments/export.csv?campid=8");
					} else if (campName.equals("B")) {
						url = new URL("http://www.ctyc.org.hk/summer/enrollments/export.csv?campid=9");
					}
					
					print(url.openStream());
					
					campSite.getParticipants().addAll(CsvReader.readParticipantCsvFromStream(url.openStream()));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				this.campSiteMap.put(campName, campSite);
			}
			
//			this.saveCampSiteToFile();
		}
	}
	
	private void print(InputStream inputStream) {
		String line = null;
		int count = 0;
		
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				count++;
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Loading time: " + new Date().toString());
		System.out.println("Head count: " + count);
		System.out.println();
	}
	
	private void initDineAssignmentPlanMap() {
		// force refreshing dine assignment
//		String resourcePath = "/DineAssignmentPlan.txt";
//		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
//		this.dineAssignmentPlanList = FileUtils.readInputStreamToObject(inputStream);
				
		// force refreshing dine assignment
		this.dineAssignmentPlanList = null;
		
		if (this.dineAssignmentPlanList == null){
			
			this.dineAssignmentPlanList = new ArrayList<DineAssignmentPlan>();
			Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
			costFunctions.add(new GenderBalanceCostFunction(1, 1));
			costFunctions.add(new SameGroupCostFunction(1, 1));
//			costFunctions.add(new SameSundayClassCostFunction(1, 1));
			
			Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
			constraintFunctions.add(new MentorInTableCostFunction(1, 1));
			constraintFunctions.add(new FamilyGroupCostFunction(1, 1));
			
			for (String campName : campNames){
				
				CampSite campSite = this.campSiteMap.get(campName);
				if (campSite == null){
					continue;
				}
				
				// Generate 4 days of dine assignment plan
				for (int i=0; i<4; i++){
					
					DineAssignmentManager dineAssignmentManager =
							new DineAssignmentManager(campName, i + 1, campSite.getParticipants(), 8, costFunctions, constraintFunctions, i);
					dineAssignmentManager.doAssignment();
					DineAssignmentPlan dineAssignmentPlan = dineAssignmentManager.getAssignmentPlan();
					
					if (dineAssignmentPlan != null){
						this.dineAssignmentPlanList.add(dineAssignmentPlan);
					}
				}
			}
		}
		
//		this.saveDineTableAssignmentToFile();
	}
	
	private void initParticipantMap(){
		this.participantMap = new HashMap<String, Participant>();
		
		if (this.dineAssignmentPlanList == null){
			return;
		}
		
		for (DineAssignmentPlan dineAssignmentPlan : this.dineAssignmentPlanList){
			for (DineTableGroup dineTableGroup : dineAssignmentPlan.getDineTableGroups()){
				for (Participant participant : dineTableGroup.getParticipants()){
					
					participant.setDineTableNumber(
							dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.NIGHT.toString(), dineTableGroup.getTableNumber());
					participant.setDineTableNumber(
							dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.NOON.toString(), dineTableGroup.getTableNumber());
					participant.setDineTableNumber(
							dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.MORNING.toString(), dineTableGroup.getTableNumber());
					
					this.participantMap.put(participant.getId(), participant);
				}
			}
		}
	}
	
	private void updateTableNumberOfEachParticipant(){
		
		if (this.dineAssignmentPlanList == null){
			return;
		}
		
		for (DineAssignmentPlan dineAssignmentPlan : this.dineAssignmentPlanList){
			for (DineTableGroup dineTableGroup : dineAssignmentPlan.getDineTableGroups()){
				for (Participant participant : dineTableGroup.getParticipants()){
					
					System.out.printf("Table Number: %s\n", dineTableGroup.getTableNumber());
					
					participant.setDineTableNumber(
							dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.NIGHT.toString(), dineTableGroup.getTableNumber());
					participant.setDineTableNumber(
							dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.NOON.toString(), dineTableGroup.getTableNumber());
					participant.setDineTableNumber(
							dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.MORNING.toString(), dineTableGroup.getTableNumber());
					
					this.participantMap.put(participant.getId(), participant);
				}
			}
		}
	}
	
	protected void saveCampSiteToFile(){
		FileUtils.writeObjectToFile(this.campSiteMap, CAMP_SITE_PATH);
	}
	
	protected void saveDineTableAssignmentToFile(){
		FileUtils.writeObjectToFile(this.dineAssignmentPlanList, DINE_ASSIGNMENT_PLAN_PATH);
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
			data.put("dineAssignmentPlans", this.dineAssignmentPlanList);
			data.put("groupAssignmentPlans", this.constructGroupAssignmentPlan());
			data.put("dineAssignmentStatistics", this.generateDineAssignmentStatistics());
			responseMessage = new Message(DINE_ASSIGNMENT_DATA, data);
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), UPDATE_DINE_TABLE)){
			responseMessage = this.updateDineTable(requestMessage.getData());
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), UPDATE_DINE_ASSIGNMENT)){
			responseMessage = this.updateDineAssignment(requestMessage.getData());
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), AUTO_ASSIGN)){
			responseMessage = this.autoDineAssignment(requestMessage.getData());
		}
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), CALCULATE_COST)){
			responseMessage = this.calculateCost(requestMessage.getData());
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
		
		if (data == null || data.get("camp") == null || data.get("day") == null || data.get("dineTableGroups") == null){
			return null;
		}
		
		String campName = data.get("camp").toString();
		int day = Integer.parseInt(data.get("day").toString());
		DineAssignmentPlan dineAssignmentPlan = this.findDineAssignmentPlan(campName, day);
		
		if (dineAssignmentPlan == null){
			return null;
		}
		dineAssignmentPlan.getDineTableGroups().clear();
		
		List<Map<String, Object>> dineTableGroupsDataList = (List<Map<String, Object>>) data.get("dineTableGroups");
		
		for (Map<String, Object> dineTableGroupsData : dineTableGroupsDataList){
			Integer tableNumber = Integer.valueOf(dineTableGroupsData.get("tableNumber").toString());
			List<Map<String, Object>> participantDataList = (List<Map<String, Object>>) dineTableGroupsData.get("participants");
			
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(tableNumber);
			
			for (Map<String, Object> participantData : participantDataList){
				Participant participant = this.participantMap.get(participantData.get("id").toString());
				dineTableGroup.getParticipants().add(participant);
			}
			
			dineAssignmentPlan.getDineTableGroups().add(dineTableGroup);
		}
		
		this.updateTableNumberOfEachParticipant();
		this.saveDineTableAssignmentToFile();
		
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("isSuccess", true);
		return new Message("UPDATE_DINE_ASSIGNMENT_COMPLETE", responseData);
	}
	
	private Message autoDineAssignment(Map<String, Object> data){
		init();
		
		if (data == null
				|| data.get("camp") == null
				|| data.get("day") == null
				|| data.get("tableCapacity") == null
//				|| data.get("constraints") == null
				|| data.get("seed") == null){
			return null;
		}
		
		String campName = data.get("camp").toString();
		int day = Integer.parseInt(data.get("day").toString());
		
		int tableCapacity = Integer.valueOf(data.get("tableCapacity").toString());
		int seed = Integer.valueOf(data.get("seed").toString());
//		Map<String, Object> constraintsMap = (Map<String, Object>)data.get("constraints");
		
		System.out.println("Auto assign summer camp " + campName);
		
		CampSite campSite = this.campSiteMap.get(campName);
		if (campSite == null){
			return null;
		}
		
//		Collection<AbstractCostFunction> costFunctions = this.createCostFunctions(constraintsMap);
//		Collection<AbstractCostFunction> constraintFunctions = this.createConstraintFunctions(constraintsMap);
		
		Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
		costFunctions.add(new GenderBalanceCostFunction(1, 1));
		costFunctions.add(new SameGroupCostFunction(1, 1));
		
		Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
		constraintFunctions.add(new MentorInTableCostFunction(1, 1));
		constraintFunctions.add(new FamilyGroupCostFunction(1, 1));
		
		DineAssignmentManager dineAssignmentManager =
				new DineAssignmentManager(campName, day, campSite.getParticipants(), tableCapacity, costFunctions, constraintFunctions, seed);
		
		dineAssignmentManager.doAssignment();
		DineAssignmentPlan dineAssignmentPlan = dineAssignmentManager.getAssignmentPlan();
		
		if (dineAssignmentPlan != null){
			this.dineAssignmentPlanList.add(dineAssignmentPlan);
		}
		
//		this.saveDineTableAssignmentToFile();
		
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("dineAssignmentPlan", dineAssignmentPlan);
		responseData.put("isSuccess", true);
		return new Message(AUTO_ASSIGN_COMPLETE, responseData);
	}
	
	private Collection<AbstractCostFunction> createCostFunctions(Map<String, Object> constraintMap){
		Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
		
		if (Boolean.valueOf(constraintMap.get("genderBalance").toString())){
			costFunctions.add(new GenderBalanceCostFunction(1, 1));
		}
		
		if (Boolean.valueOf(constraintMap.get("sameSundayClass").toString())){
			costFunctions.add(new SameSundayClassCostFunction(1, 1));
		}
		
		return costFunctions;
	}
	
	private Collection<AbstractCostFunction> createConstraintFunctions(Map<String, Object> constraintMap){
		Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
		
		if (Boolean.valueOf(constraintMap.get("mentorInTable").toString())){
			constraintFunctions.add(new MentorInTableCostFunction(1, 1));
		}
		
		if (Boolean.valueOf(constraintMap.get("familySameTable").toString())){
			constraintFunctions.add(new FamilyGroupCostFunction(1, 1));
		}

		return constraintFunctions;
	}
	
	private Message calculateCost(Map<String, Object> data) {
		if (data == null ||  data.get("camp") == null || data.get("day") == null || data.get("dineTableGroups") == null){
			return null;
		}
		
		String campName = data.get("camp").toString();
		int day = Integer.parseInt(data.get("day").toString());
		DineAssignmentPlan dineAssignmentPlan = new DineAssignmentPlan(campName, day);
		
		List<Map<String, Object>> dineTableGroupsDataList = (List<Map<String, Object>>) data.get("dineTableGroups");
		
		for (Map<String, Object> dineTableGroupsData : dineTableGroupsDataList){
			Integer tableNumber = Integer.valueOf(dineTableGroupsData.get("tableNumber").toString());
			List<Map<String, Object>> participantDataList = (List<Map<String, Object>>) dineTableGroupsData.get("participants");
			
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(tableNumber);
			
			for (Map<String, Object> participantData : participantDataList){
				Participant participant = this.participantMap.get(participantData.get("id").toString());
				dineTableGroup.getParticipants().add(participant);
			}
			
			dineAssignmentPlan.getDineTableGroups().add(dineTableGroup);
		}
		
		Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
		costFunctions.add(new GenderBalanceCostFunction(1, 1));
		costFunctions.add(new SameGroupCostFunction(1, 1));
		
		Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
		constraintFunctions.add(new MentorInTableCostFunction(1, 1));
		constraintFunctions.add(new FamilyGroupCostFunction(1, 1));
		
		DineAssignmentEvaluator evaluator = new DineAssignmentEvaluator(costFunctions, constraintFunctions);
		evaluator.evaluatePlan(dineAssignmentPlan);
		
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("dineAssignmentPlan", dineAssignmentPlan);
		responseData.put("camp", campName);
		responseData.put("day", day);
		responseData.put("isSuccess", true);
		return new Message(CALCULATE_COST_COMPLETE, responseData);
	}
	
	private DineAssignmentPlan findDineAssignmentPlan(String campSiteName, int day){
		if (this.dineAssignmentPlanList == null){
			return null;
		}
		
		for (DineAssignmentPlan dineAssignmentPlan : this.dineAssignmentPlanList){
			if (StringUtils.equalsIgnoreCase(campSiteName, dineAssignmentPlan.getCampName()) &&
					day == dineAssignmentPlan.getDay()){
				return dineAssignmentPlan;
			}
		}
		return null;
	}
	
	private Map<String, GroupAssignmentPlan> constructGroupAssignmentPlan() {
		
		if (participantMap == null){
			return new HashMap<String, GroupAssignmentPlan>();
		}
		
		Map<String, GroupAssignmentPlan> groupAssignmentPlanMap = new HashMap<String, GroupAssignmentPlan>();
		
		for (Participant participant : participantMap.values()){
			
			String camp = participant.getId().substring(0, 1);
			String key = camp + "-" + String.valueOf(participant.getGroupNumber());
			
			if (groupAssignmentPlanMap.get(key) == null){
				GroupAssignmentPlan tempGroupAssignmentPlan = new GroupAssignmentPlan(camp, participant.getGroupNumber());
				tempGroupAssignmentPlan.getParticipants().add(participant);
				groupAssignmentPlanMap.put(key, tempGroupAssignmentPlan);
			}else {
				groupAssignmentPlanMap.get(key).getParticipants().add(participant);
			}
		}
		
		return groupAssignmentPlanMap;
	}
	
	private Map<String, Map<String, DineTimeStatistics>> generateDineAssignmentStatistics() {
		
		if (this.dineAssignmentPlanList == null){
			return null;
		}
		
		Map<String, Map<String, DineTimeStatistics>> campDineTableStatistics = new HashMap<String, Map<String, DineTimeStatistics>>();
		for (DineAssignmentPlan dineAssignmentPlan : this.dineAssignmentPlanList){
			
			String campName = dineAssignmentPlan.getCampName();
			Map<String, DineTimeStatistics> tempDineTimeStatistics = campDineTableStatistics.get(campName);
			if (tempDineTimeStatistics == null){
				tempDineTimeStatistics = new HashMap<String, DineTimeStatistics>();
				campDineTableStatistics.put(campName, tempDineTimeStatistics);
			}
			
			for (DineTableGroup dineTableGroup : dineAssignmentPlan.getDineTableGroups()){
				
				// Count the number of participant at Night for that table
				DineTimeStatistics tempDineTableStatistics = tempDineTimeStatistics.get(DineTimeSlot.TimeOfDay.NIGHT.toString());
				if (tempDineTableStatistics == null){
					tempDineTableStatistics = new DineTimeStatistics(DineTimeSlot.TimeOfDay.NIGHT.toString());
					tempDineTimeStatistics.put(DineTimeSlot.TimeOfDay.NIGHT.toString(), tempDineTableStatistics);
				}

				tempDineTableStatistics.setDineTableStatistics(
						dineTableGroup.getTableNumber(),
						dineAssignmentPlan.getDay(),
						dineTableGroup.countParticipantForParticularDine(dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.NIGHT.toString()));
				
				// Count the number of participant at Morning for that table
				tempDineTableStatistics = tempDineTimeStatistics.get(DineTimeSlot.TimeOfDay.MORNING.toString());
				if (tempDineTableStatistics == null){
					tempDineTableStatistics = new DineTimeStatistics(DineTimeSlot.TimeOfDay.MORNING.toString());
					tempDineTimeStatistics.put(DineTimeSlot.TimeOfDay.MORNING.toString(), tempDineTableStatistics);
				}

				tempDineTableStatistics.setDineTableStatistics(
						dineTableGroup.getTableNumber(),
						dineAssignmentPlan.getDay(),
						dineTableGroup.countParticipantForParticularDine(dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.MORNING.toString()));
				
				// Count the number of participant at Noon for that table
				tempDineTableStatistics = tempDineTimeStatistics.get(DineTimeSlot.TimeOfDay.NOON.toString());
				if (tempDineTableStatistics == null){
					tempDineTableStatistics = new DineTimeStatistics(DineTimeSlot.TimeOfDay.NOON.toString());
					tempDineTimeStatistics.put(DineTimeSlot.TimeOfDay.NOON.toString(), tempDineTableStatistics);
				}

				tempDineTableStatistics.setDineTableStatistics(
						dineTableGroup.getTableNumber(),
						dineAssignmentPlan.getDay(),
						dineTableGroup.countParticipantForParticularDine(dineAssignmentPlan.getDay(), DineTimeSlot.TimeOfDay.NOON.toString()));
			}
		}
		
		return campDineTableStatistics;
	}
	
}
