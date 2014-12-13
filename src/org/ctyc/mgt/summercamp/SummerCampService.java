package org.ctyc.mgt.summercamp;

import java.util.HashMap;
import java.util.Map;

import org.ctyc.mgt.model.summercamp.CampSite;

public class SummerCampService {
	
	private static SummerCampService instance = null;
	private Map<String, CampSite> campSiteMap = new HashMap<String, CampSite>();

	protected SummerCampService(){
		
	}
	
	public static SummerCampService getInstance() {
		if(instance == null) {
			instance = new SummerCampService();
		}
		return instance;
	}
	
	
}
