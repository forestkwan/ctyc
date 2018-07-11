package org.ctyc.mgt.model.summercamp;

import java.util.HashMap;
import java.util.Map;

public class CampDineStatistics {

	private Map<CampName, Map<String, DineTimeStatistics>> campDineTimeStatisticsMap;

	public Map<CampName, Map<String, DineTimeStatistics>> getCampDineTimeStatisticsMap() {
		if (this.campDineTimeStatisticsMap == null){
			this.campDineTimeStatisticsMap = new HashMap<>();
		}
		return this.campDineTimeStatisticsMap;
	}	
}
