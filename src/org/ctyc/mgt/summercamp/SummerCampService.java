package org.ctyc.mgt.summercamp;

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
import org.ctyc.mgt.utils.CsvReader;
import org.ctyc.mgt.utils.FileUtils;
import org.ctyc.mgt.websocket.Message;

public class SummerCampService {
	
	private static String SERVER_RESPONSE = "SERVER_RESPONSE";
	private static String GET_CAMP_SITE = "GET_CAMP_SITE";
	private static String CAMP_SITE_DATA = "CAMP_SITE_DATA";
	private static String UPDATE_DINE_TABLE = "UPDATE_DINE_TABLE";
	
	private static String CAMP_SITE_PATH = "c:\\CTYCSave\\CampSite.txt";
	
	private static SummerCampService instance = null;
	private Map<String, CampSite> campSiteMap = null;

	protected SummerCampService(){
		// Load saved camp site
		this.campSiteMap = FileUtils.readFileToObject(CAMP_SITE_PATH);
		if (this.campSiteMap == null){
			this.campSiteMap = new HashMap<String, CampSite>();
			
			CampSite campA = new CampSite();
			campA.setName("A");
			campA.getParticipants().addAll(CsvReader.readParticipantCsv("c:\\CTYCSave\\campA_panticipants.csv"));
			
			CampSite campB = new CampSite();
			campB.setName("B");
			campB.getParticipants().addAll(CsvReader.readParticipantCsv("c:\\CTYCSave\\campB_panticipants.csv"));
			
			this.campSiteMap.put("A", campA);
			this.campSiteMap.put("B", campB);
			
			this.saveCampSiteToFile();
		}
	}
	
	protected void saveCampSiteToFile(){
		FileUtils.writeObjectToFile(this.campSiteMap, CAMP_SITE_PATH);
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
		
		if (StringUtils.equalsIgnoreCase(requestMessage.getType(), UPDATE_DINE_TABLE)){
			responseMessage = this.updateDineTable(requestMessage.getData());
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
	
}
