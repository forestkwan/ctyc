package org.ctyc.mgt.summercamp;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.utils.FileUtils;
import org.ctyc.mgt.websocket.Message;

public class SummerCampService {
	
	private static String GET_CAMP_SITE = "GET_CAMP_SITE";
	private static String CAMP_SITE_DATA = "CAMP_SITE_DATA";
	
	private static String CAMP_SITE_PATH = "CampSite.txt";
	
	private static SummerCampService instance = null;
	private Map<String, CampSite> campSiteMap = null;

	protected SummerCampService(){
		// Load saved camp site
		this.campSiteMap = FileUtils.readFileToObject(CAMP_SITE_PATH);
		if (this.campSiteMap == null){
			this.campSiteMap = new HashMap<String, CampSite>();
			FileUtils.writeObjectToFile(this.campSiteMap, CAMP_SITE_PATH);
		}
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
			data.put("campSite", this.campSiteMap);
			responseMessage = new Message(CAMP_SITE_DATA, data);
		}
		
		return responseMessage;
	}
	
}
